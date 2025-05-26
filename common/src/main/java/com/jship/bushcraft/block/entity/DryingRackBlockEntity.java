package com.jship.bushcraft.block.entity;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.spiritapi.api.item.SpiritItemStorage;
import com.jship.spiritapi.api.item.SpiritItemStorage.SlotConfig;
import com.jship.spiritapi.api.item.SpiritItemStorageProvider;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

@Slf4j
public class DryingRackBlockEntity extends BlockEntity implements SpiritItemStorageProvider {

    public static final int SLOT_COUNT = 4;
    public int[] dryingProgress = new int[SLOT_COUNT];
    public int[] dryingTime = new int[SLOT_COUNT];
    public int[] finishedDrying = new int[SLOT_COUNT];

    private final SlotConfig slotConfig = new SpiritItemStorage.SlotConfig(true, true, 1, stack -> true);
    private final SpiritItemStorage itemStorage = SpiritItemStorage.create(
            // Should be SLOT_COUNT elements
            List.of(slotConfig, slotConfig, slotConfig, slotConfig),
            this::markUpdated);

    private final RecipeManager.CachedCheck<SingleRecipeInput, DryingRecipe> quickCheck = RecipeManager
            .createCheck(ModRecipes.DRYING.get());

    public DryingRackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.DRYING_RACK.get(), blockPos, blockState);
    }

    public static void dryTick(Level level, BlockPos blockPos, BlockState blockState,
            DryingRackBlockEntity dryingRackEntity) {
        boolean itemsChanged = false;

        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = dryingRackEntity.itemStorage.getStackInSlot(i);
            if (stack.isEmpty()) {
                dryingRackEntity.dryingProgress[i] = 0;
                dryingRackEntity.dryingTime[i] = 0;
                dryingRackEntity.finishedDrying[i] = 0;
            } else {
                itemsChanged = true;
                val recipe = dryingRackEntity.getRecipeFor(stack);
                if (dryingRackEntity.dryingProgress[i] == 0 && dryingRackEntity.dryingTime[i] == 0
                        && recipe.isPresent()) {
                    dryingRackEntity.dryingTime[i] = recipe.get().value().time();
                    dryingRackEntity.finishedDrying[i] = 0;
                }
                dryingRackEntity.dryingProgress[i] = Math.min(dryingRackEntity.dryingProgress[i] + 1,
                        dryingRackEntity.dryingTime[i]);
                if (dryingRackEntity.dryingProgress[i] >= dryingRackEntity.dryingTime[i]
                        && dryingRackEntity.finishedDrying[i] <= 0) {
                    SingleRecipeInput input = new SingleRecipeInput(stack);
                    Optional<ItemStack> result = dryingRackEntity.quickCheck
                            .getRecipeFor(input, level)
                            .map(recipeHolder -> {
                                return recipeHolder.value().assemble(input, level.registryAccess());
                            });
                    if (result.isPresent() && result.get().isItemEnabled(level.enabledFeatures())) {
                        dryingRackEntity.itemStorage.extractItem(i, 1, false);
                        dryingRackEntity.itemStorage.insertItem(i, result.get(), false);
                        dryingRackEntity.finishedDrying[i] = 1;
                        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(blockState));
                    }
                }
            }
        }

        if (itemsChanged)
            setChanged(level, blockPos, blockState);
    }

    public boolean hasFinishedStacks() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!itemStorage.getStackInSlot(i).isEmpty() && finishedDrying[i] > 0)
                return true;
        }
        return false;
    }

    public void dropFinishedStacks() {
        for (var i = 0; i < SLOT_COUNT; i++) {
            val stack = itemStorage.extractItem(i, itemStorage.getStackInSlot(i).getCount(), false);
            if (!stack.isEmpty() && finishedDrying[i] > 0) {
                val blockPos = this.getBlockPos();
                Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
            }
        }
    }

    public Optional<RecipeHolder<DryingRecipe>> getRecipe(ItemStack stack) {
        var outputSlotAvailable = false;

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (itemStorage.getStackInSlot(i).isEmpty())
                outputSlotAvailable = true;
        }

        return outputSlotAvailable ? getRecipeFor(stack) : Optional.empty();
    }

    public Optional<RecipeHolder<DryingRecipe>> getRecipeFor(ItemStack stack) {
        return quickCheck.getRecipeFor(new SingleRecipeInput(stack), this.level);
    }

    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compoundTag, lookupProvider);
        if (compoundTag.contains("Items"))
            this.itemStorage.deserializeNbt(lookupProvider, compoundTag);
        if (compoundTag.contains("DryingTimes"))
            this.dryingProgress = compoundTag.getIntArray("DryingTimes").clone();
        if (compoundTag.contains("DryingTotalTimes"))
            this.dryingTime = compoundTag.getIntArray("DryingTotalTimes").clone();
        if (compoundTag.contains("FinishedDryings"))
            this.finishedDrying = compoundTag.getIntArray("FinishedDryings").clone();
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        compoundTag.merge(this.itemStorage.serializeNbt(lookupProvider));
        compoundTag.putIntArray("DryingTimes", dryingProgress);
        compoundTag.putIntArray("DryingTotalTimes", dryingTime);
        compoundTag.putIntArray("FinishedDryings", finishedDrying);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.merge(this.itemStorage.serializeNbt(lookupProvider));
        return compoundTag;
    }

    public boolean hasUnfinishedItem() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!itemStorage.getStackInSlot(i).isEmpty() && finishedDrying[i] <= 0)
                return true;
        }
        return false;
    }

    public ItemStack removeUnfinishedItem() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack removedStack = itemStorage.getStackInSlot(i);
            if (!removedStack.isEmpty() && finishedDrying[i] <= 0) {
                return itemStorage.extractItem(i, itemStorage.getStackInSlot(i).getCount(), false);
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean placeItem(@Nullable LivingEntity livingEntity, ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            val insertedStack = itemStorage.insertItem(i, stack.copy(), true);
            if (insertedStack.getCount() < stack.getCount()) {
                val recipe = getRecipeFor(stack);
                dryingTime[i] = recipe.isPresent() ? recipe.get().value().time() : 0;
                dryingProgress[i] = 0;
                finishedDrying[i] = 0;
                val playerStack = livingEntity != null && livingEntity.hasInfiniteMaterials() ? stack.copy() : stack;
                itemStorage.insertItem(i, playerStack, false);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), Context.of(livingEntity, getBlockState()));
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        if (this.getLevel() == null || this.getLevel().isClientSide())
            return;
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public @Nullable SpiritItemStorage getItemStorage(Direction face) {
        return this.itemStorage;
    }
}
