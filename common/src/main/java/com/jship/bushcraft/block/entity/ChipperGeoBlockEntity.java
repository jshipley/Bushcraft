package com.jship.bushcraft.block.entity;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jship.bushcraft.block.ChipperBlock;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.menu.ChipperMenu;
import com.jship.bushcraft.recipe.ChippingRecipe;
import com.jship.spiritapi.api.item.SpiritItemStorage;
import com.jship.spiritapi.api.item.SpiritItemStorage.SlotConfig;
import com.jship.spiritapi.api.item.SpiritItemStorageProvider;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@Slf4j
@Accessors(fluent = true)
public class ChipperGeoBlockEntity extends BlockEntity
        implements GeoBlockEntity, SpiritItemStorageProvider, RecipeCraftingHolder, ExtendedMenuProvider, StackedContentsCompatible {

    public static final String ANIM_NAME = "chipper_animation";
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("animation.model.idle");
    public static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("animation.model.working");

    public final SpiritItemStorage itemStorage = SpiritItemStorage
            .create(ImmutableList.of(
                    SlotConfig.builder()
                            .validItem(stack -> this.level != null
                                    && quickCheck.getRecipeFor(new SingleRecipeInput(stack), this.level).isPresent())
                            .canInsert(direction -> true).canExtract(direction -> false).build(),
                    SlotConfig.builder().canInsert(direction -> direction.getAxis().isHorizontal()).canExtract(direction -> false)
                            .validItem(stack -> AbstractFurnaceBlockEntity.isFuel(stack)).build(),
                    SlotConfig.builder().canInsert(direction -> false).playerInsert(false).build(),
                    SlotConfig.builder().canInsert(direction -> false).playerInsert(false).build()), this::setChanged);
    public static final double SECONDS_PER_CYCLE = 2.0d;

    private static final RecipeManager.CachedCheck<SingleRecipeInput, ChippingRecipe> quickCheck = RecipeManager
            .createCheck(ModRecipes.CHIPPING.get());

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static int INPUT_SLOT = 0;
    public static int FUEL_SLOT = 1;
    public static int OUTPUT_SLOT = 2;
    public static int BONUS_SLOT = 3;

    @Getter
    protected int litTime = 0;
    @Getter
    protected int litTotalTime = 0;
    @Getter
    protected int assembleTime = 0;
    @Getter
    protected int assembleTotalTime = 0;
    protected RecipeHolder<ChippingRecipe> activeRecipe;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;

    public ChipperGeoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CHIPPER.get(), pos, blockState);
        recipesUsed = new Object2IntOpenHashMap<>();
    }

    public boolean isLit() { return litTime > 0; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChipperGeoBlockEntity blockEntity) {
        var dirty = false;
        var wasLit = false;

        if (blockEntity.isLit()) {
            wasLit = true;
            blockEntity.litTime--;
        }

        if (level == null || level.isClientSide())
            return;

        val inputStack = blockEntity.itemStorage.getStackInSlot(INPUT_SLOT);
        val fuelStack = blockEntity.itemStorage.getStackInSlot(FUEL_SLOT);
        val outputStack = blockEntity.itemStorage.getStackInSlot(OUTPUT_SLOT);
        val bonusStack = blockEntity.itemStorage.getStackInSlot(BONUS_SLOT);

        if (inputStack.isEmpty() && blockEntity.assembleTime > 0) {
            blockEntity.resetProgress(null);
        }

        // there's an item to process, and heat to do it with
        if (!inputStack.isEmpty() && (blockEntity.isLit() || !fuelStack.isEmpty())) {

            val recipeInput = new SingleRecipeInput(inputStack);
            val recipe = quickCheck.getRecipeFor(recipeInput, level);
            if (recipe.isPresent()) {
                if (blockEntity.activeRecipe == null || blockEntity.activeRecipe.value() == null
                        || !blockEntity.activeRecipe.value().matches(recipeInput, level))
                    blockEntity.resetProgress(recipe.get());
            }

            if (canAssemble(recipe, blockEntity.itemStorage)) {
                if (!blockEntity.isLit()) {
                    val fuelTime = FuelRegistry.get(fuelStack);
                    if (fuelTime > 0) {

                        dirty = true;
                        blockEntity.litTime = fuelTime;
                        blockEntity.litTotalTime = fuelTime;
                        val removedFuel = blockEntity.itemStorage.extractItem(FUEL_SLOT, 1, false);
                        if (blockEntity.itemStorage.getStackInSlot(FUEL_SLOT).isEmpty() && removedFuel.getItem().hasCraftingRemainingItem())
                            blockEntity.itemStorage.insertItem(FUEL_SLOT, new ItemStack(removedFuel.getItem().getCraftingRemainingItem()),
                                    false);
                    }
                }

                if (blockEntity.isLit()) {
                    blockEntity.assembleTime++;
                    dirty = true;
                    if (blockEntity.assembleTime >= blockEntity.assembleTotalTime) {
                        if (assemble(recipe, blockEntity.itemStorage, level.getRandom())) {
                            blockEntity.setRecipeUsed(recipe.get());
                            blockEntity.resetProgress(null);
                        }
                    }
                }
            }
        }

        if (wasLit != blockEntity.isLit())
            level.setBlock(pos, state.setValue(ChipperBlock.LIT, blockEntity.isLit()), 3);

        if (dirty) {
            blockEntity.setChanged();
        }
    }

    private static boolean canAssemble(Optional<RecipeHolder<ChippingRecipe>> recipe, SpiritItemStorage itemStorage) {
        if (itemStorage.getStackInSlot(INPUT_SLOT).isEmpty() || !recipe.isPresent())
            return false;

        val resultStack = recipe.get().value().result();
        if (resultStack.isEmpty())
            return false;

        return itemStorage.insertItem(OUTPUT_SLOT, resultStack, true).isEmpty();
    }

    private static boolean assemble(Optional<RecipeHolder<ChippingRecipe>> recipe, SpiritItemStorage itemStorage, RandomSource random) {
        if (recipe.isPresent() && canAssemble(recipe, itemStorage)) {
            val resultStack = recipe.get().value().result();

            itemStorage.extractItem(INPUT_SLOT, 1, false);
            itemStorage.insertItem(OUTPUT_SLOT, resultStack, false);
            val chanceResult = recipe.get().value().chanceResult();
            if (!chanceResult.isEmpty() && random.nextFloat() <= chanceResult.chance()) {
                // If bonus slot is full, then I guess there's no random bonus for you.
                itemStorage.insertItem(BONUS_SLOT, chanceResult.result(), false);
            }
            return true;
        }
        return false;
    }

    private void resetProgress(@Nullable RecipeHolder<ChippingRecipe> recipe) {
        this.activeRecipe = recipe;
        this.assembleTime = 0;
        this.assembleTotalTime = recipe == null ? 0 : recipe.value().time();
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, ANIM_NAME, (anim) -> anim.setAndContinue(IDLE_ANIM))
                .triggerableAnim("chip", WORKING_ANIM).setAnimationSpeed(1.0d / SECONDS_PER_CYCLE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        if (nbt.contains("Items")) {
            this.itemStorage.deserializeNbt(registryLookup, nbt);
        }
        this.litTime = nbt.getShort("LitTime");
        this.litTotalTime = nbt.getShort("LitTotalTime");
        this.assembleTime = nbt.getShort("AssembleTime");
        this.assembleTotalTime = nbt.getShort("AssembleTotalTime");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.merge(this.itemStorage.serializeNbt(registryLookup));
        nbt.putShort("LitTime", (short) this.litTime);
        nbt.putShort("LitTotalTime", (short) this.litTotalTime);
        nbt.putShort("AssembleTime", (short) this.assembleTime);
        nbt.putShort("AssembleTotalTime", (short) assembleTotalTime);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbt = super.getUpdateTag(registryLookup);
        saveAdditional(nbt, registryLookup);
        return nbt;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        // doing this the "right" way crashes on Fabric, but this way works
        val buf = new FriendlyByteBuf(ByteBufAllocator.DEFAULT.buffer());
        saveExtraData(buf);
        return new ChipperMenu(id, inventory, buf);
    }

    @Override
    public Component getDisplayName() { return Component.translatable("container.bushcraft.chipper"); }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.getLevel() != null && !this.getLevel().isClientSide())
            this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    public @Nullable SpiritItemStorage getItemStorage(Direction face) { return this.itemStorage; }

    /*
     * Reward experience
     */

    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourceLocation = recipe.id();
            this.recipesUsed.addTo(resourceLocation, 1);
        }

    }

    @Nullable
    public RecipeHolder<?> getRecipeUsed() { return null; }

    public void awardUsedRecipes(Player player, List<ItemStack> items) {}

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);

        for (var recipeHolder : list) {
            if (recipeHolder != null) {
                // not sure if this is right...
                player.triggerRecipeCrafted(recipeHolder, NonNullList.of(ItemStack.EMPTY, this.itemStorage.getStackInSlot(INPUT_SLOT)));
            }
        }

        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
        List<RecipeHolder<?>> list = Lists.newArrayList();
        val recipeIter = this.recipesUsed.object2IntEntrySet().iterator();

        for (var recipeEntry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(recipeEntry.getKey()).ifPresent(recipeHolder -> {
                list.add(recipeHolder);
                createExperience(level, popVec, recipeEntry.getIntValue(), ((AbstractCookingRecipe) recipeHolder.value()).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
        int i = Mth.floor((float) recipeIndex * experience);
        float f = Mth.frac((float) recipeIndex * experience);
        if (f > 0.001f && Math.random() < (double) f) {
            ++i;
        }

        ExperienceOrb.award(level, popVec, i);
    }

    @Override
    public void saveExtraData(FriendlyByteBuf buf) { buf.writeBlockPos(this.getBlockPos()); }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for (int i = 0; i < itemStorage.getSlots(); i++) {
            val stack = itemStorage.getStackInSlot(i);
            contents.accountStack(stack);
        }
    }
}
