package com.jship.bushcraft.block.entity;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.init.ModTags.ModBlockTags;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.FluidStackRecipeInput;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.spiritapi.api.fluid.SpiritFluidStorage;
import com.jship.spiritapi.api.fluid.SpiritFluidStorageProvider;

import dev.architectury.fluid.FluidStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class CrucibleBlockEntity extends BlockEntity implements WorldlyContainer, SpiritFluidStorageProvider {

    private static final int SLOT_COUNT = 1;
    private static final int[] SLOTS = { 0 };
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    public int workProgress = 0;
    public int workTime = 0;
    public int workRate = 0;
    public ThermalMode workMode = ThermalMode.NEUTRAL;

    public final SpiritFluidStorage fluidStorage = SpiritFluidStorage.create(FluidStack.bucketAmount(),
            FluidStack.bucketAmount(),
            this::markUpdated);

    private static final RecipeManager.CachedCheck<SingleRecipeInput, MeltingRecipe> meltQuickCheck = RecipeManager
            .createCheck(ModRecipes.MELTING.get());

    public CrucibleBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CRUCIBLE.get(), blockPos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
            CrucibleBlockEntity crucibleEntity) {
        if (level.isClientSide())
            return;

        val heatLevel = heatLevel(level, pos);
        val coldLevel = coldLevel(level, pos);

        if (heatLevel > coldLevel) {
            crucibleEntity.workRate = heatLevel - coldLevel;
            crucibleEntity.workMode = ThermalMode.MELTING;
            meltTick(level, pos, state, crucibleEntity);
        } else if (coldLevel > heatLevel) {
            crucibleEntity.workRate = coldLevel - heatLevel;
            crucibleEntity.workMode = ThermalMode.COOLING;
            coolTick(level, pos, state, crucibleEntity);
        } else {
            crucibleEntity.workRate = 0;
            crucibleEntity.workMode = ThermalMode.NEUTRAL;
        }
    }

    private static void meltTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucibleEntity) {
        val stack = crucibleEntity.items.get(0);
        // no item or full tank
        if (stack.isEmpty() || crucibleEntity.fluidStorage.getFluidInTank(0).getAmount() >= crucibleEntity.fluidStorage
                .getTankCapacity(0)) {
            crucibleEntity.resetProgress();
            return;
        }

        val recipeHolder = meltQuickCheck.getRecipeFor(new SingleRecipeInput(stack), level);
        if (recipeHolder.isPresent()) {
            crucibleEntity.workTime = recipeHolder.get().value().time();
            crucibleEntity.workProgress = Math.min(
                    crucibleEntity.workProgress + crucibleEntity.workRate,
                    crucibleEntity.workTime);
            if (crucibleEntity.workProgress >= crucibleEntity.workTime) {
                val fluidStack = recipeHolder.get().value().result();
                val inserted = crucibleEntity.fluidStorage.fill(fluidStack, true);
                if (inserted == fluidStack.getAmount()) {
                    crucibleEntity.fluidStorage.fill(fluidStack, false);
                    crucibleEntity.items.set(0, ItemStack.EMPTY);
                    crucibleEntity.resetProgress();
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(state));
                    crucibleEntity.markUpdated();
                }
            }
        }
    }

    private static void coolTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucibleEntity) {
        val fluidStack = crucibleEntity.fluidStorage.getFluidInTank(0);
        // no fluid or item slot not empty
        if (fluidStack.isEmpty() || !crucibleEntity.getItem(0).isEmpty()) {
            crucibleEntity.resetProgress();
            return;
        }

        val recipeHolder = level.getRecipeManager().getAllRecipesFor(ModRecipes.COOLING.get()).stream().filter(holder -> {
            val recipeFluidStack = holder.value().input();
            return fluidStack.getAmount() >= recipeFluidStack.getAmount() && fluidStack.isFluidEqual(recipeFluidStack);
        }).findFirst();
        if (recipeHolder.isPresent()) {
            crucibleEntity.workTime = recipeHolder.get().value().time();
            crucibleEntity.workProgress = Math.min(
                    crucibleEntity.workProgress + crucibleEntity.workRate,
                    crucibleEntity.workTime);
            if (crucibleEntity.workProgress >= crucibleEntity.workTime) {
                val stack = recipeHolder.get().value().result();
                val recipeFluidStack = recipeHolder.get().value().input();
                val drained = crucibleEntity.fluidStorage.drain(recipeFluidStack, true);
                if (drained.isFluidStackEqual(recipeFluidStack)) {
                    crucibleEntity.fluidStorage.drain(recipeFluidStack, false);
                    crucibleEntity.items.set(0, stack);
                    crucibleEntity.resetProgress();
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(state));
                    crucibleEntity.markUpdated();
                }
            }
        }
    }

    private void resetProgress() {
        this.workProgress = 0;
        this.workTime = -1;
    }

    public static int coldLevel(Level level, BlockPos pos) {
        var coldLevel = 0;
        if (level.getBiome(pos).value().coldEnoughToSnow(pos))
            coldLevel++;
        for (val dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlockTags.COLD_SOURCES))
                coldLevel++;
        }
        return coldLevel;
    }

    public static int heatLevel(Level level, BlockPos pos) {
        // melt twice as fast in the nether
        var heatLevel = 0;
        if (level.dimensionType().ultraWarm())
            heatLevel++;
        val belowState = level.getBlockState(pos.below());
        if (belowState.is(ModBlockTags.HIGH_HEAT_SOURCES)) {
            heatLevel += 3;
        } else if (belowState.is(ModBlockTags.MEDIUM_HEAT_SOURCES)) {
            heatLevel += 2;
        } else if (belowState.is(ModBlockTags.LOW_HEAT_SOURCES)) {
            heatLevel++;
        }
        return heatLevel;
    }

    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compoundTag, lookupProvider);
        clearContent();
        ContainerHelper.loadAllItems(compoundTag, items, lookupProvider);
        fluidStorage.deserializeNbt(lookupProvider, compoundTag);
        if (compoundTag.contains("WorkTime")) {
            this.workProgress = compoundTag.getInt("WorkTime");
        }
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        ContainerHelper.saveAllItems(compoundTag, items, true, lookupProvider);
        compoundTag.merge(fluidStorage.serializeNbt(lookupProvider));
        compoundTag.putInt("WorkTime", workProgress);
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput dataComponentInput) {
        super.applyImplicitComponents(dataComponentInput);
        dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
    }

    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, items, true, lookupProvider);
        compoundTag.merge(fluidStorage.serializeNbt(lookupProvider));
        return compoundTag;
    }

    public void removeComponentsFromTag(CompoundTag compoundTag) {
        compoundTag.remove("Items");
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack removedStack = ContainerHelper.removeItem(items, slot, count);
        markUpdated();
        return removedStack;
    }

    public void dropContents() {
        var stacksDropped = false;
        for (var i = 0; i < SLOT_COUNT; i++) {
            val stack = items.get(i);
            if (!stack.isEmpty()) {
                val pos = this.getBlockPos();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                items.set(i, ItemStack.EMPTY);
                stacksDropped = true;
            }
        }

        if (stacksDropped)
            markUpdated();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot > 0)
            throw new IllegalArgumentException();

        if (!items.get(slot).isEmpty())
            return;

        items.set(slot, stack);
    }

    public boolean placeItem(@Nullable LivingEntity livingEntity, ItemStack stack) {
        if (canPlaceItem(0, stack)) {
            resetProgress();
            items.set(0, stack.consumeAndReturn(1, livingEntity));
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), Context.of(livingEntity, getBlockState()));
            markUpdated();
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return items.get(slot).isEmpty()
                && fluidStorage.getFluidInTank(0).getAmount() < fluidStorage.getTankCapacity(0);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return canPlaceItem(0, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public SpiritFluidStorage getFluidStorage(Direction face) {
        return fluidStorage;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public enum ThermalMode {
        MELTING(0),
        COOLING(1),
        NEUTRAL(2);

        private final int value;

        public static ThermalMode of(int value) {
            switch (value) {
                case 0:
                    return MELTING;
                case 1:
                    return COOLING;
                case 2:
                    return NEUTRAL;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
