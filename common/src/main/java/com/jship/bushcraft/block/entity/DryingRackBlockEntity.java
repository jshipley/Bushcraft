package com.jship.bushcraft.block.entity;

import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.Bushcraft.ModRecipes;
import com.jship.bushcraft.recipe.DryingRecipe;
import java.util.Optional;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.jetbrains.annotations.Nullable;

public class DryingRackBlockEntity extends BlockEntity implements WorldlyContainer {

    private static final int SLOT_COUNT = 4;
    private static final int[] SLOTS = { 0, 1, 2, 3 };
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    public int[] dryingProgress = new int[SLOT_COUNT];
    public int[] dryingTime = new int[SLOT_COUNT];
    public int[] finishedDrying = new int[SLOT_COUNT];

    private final RecipeManager.CachedCheck<SingleRecipeInput, DryingRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.DRYING.get());

    public DryingRackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.DRYING_RACK.get(), blockPos, blockState);
    }

    public static void dryTick(Level level, BlockPos blockPos, BlockState blockState, DryingRackBlockEntity dryingRackEntity) {
        boolean itemsChanged = false;

        for (int i = 0; i < dryingRackEntity.items.size(); i++) {
            ItemStack stack = dryingRackEntity.items.get(i);
            if (!stack.isEmpty()) {
                itemsChanged = true;
                dryingRackEntity.dryingProgress[i] = Math.min(dryingRackEntity.dryingProgress[i] + 1, dryingRackEntity.dryingTime[i]);
                if (dryingRackEntity.dryingProgress[i] >= dryingRackEntity.dryingTime[i] && dryingRackEntity.finishedDrying[i] <= 0) {
                    SingleRecipeInput input = new SingleRecipeInput(stack);
                    Optional<ItemStack> result = dryingRackEntity.quickCheck
                        .getRecipeFor(input, level)
                        .map(recipeHolder -> {
                            return recipeHolder.value().assemble(input, level.registryAccess());
                        });
                    if (result.isPresent() && result.get().isItemEnabled(level.enabledFeatures())) {
                        dryingRackEntity.finishedDrying[i] = 1;
                        dryingRackEntity.items.set(i, result.get());
                        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(blockState));
                        dryingRackEntity.markUpdated();
                    }
                }
            }
        }

        if (itemsChanged) setChanged(level, blockPos, blockState);
    }

    public boolean hasFinishedStacks() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items.get(i).isEmpty() && finishedDrying[i] > 0) return true;
        }
        return false;
    }

    public void dropFinishedStacks(Level level, BlockPos blockPos, Player player) {
        boolean stacksDropped = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && finishedDrying[i] > 0) {
                Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
                items.set(i, ItemStack.EMPTY);
                stacksDropped = true;
            }
        }

        if (stacksDropped) {
            markUpdated();
        }
    }

    public Optional<RecipeHolder<DryingRecipe>> getRecipe(ItemStack stack) {
        return items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : getRecipeFor(stack);
    }

    public Optional<RecipeHolder<DryingRecipe>> getRecipeFor(ItemStack stack) {
        return quickCheck.getRecipeFor(new SingleRecipeInput(stack), this.level);
    }

    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compoundTag, lookupProvider);
        clearContent();
        ContainerHelper.loadAllItems(compoundTag, items, lookupProvider);
        if (compoundTag.contains("DryingTimes")) {
            this.dryingProgress = compoundTag.getIntArray("DryingTimes").clone();
        }
        if (compoundTag.contains("DryingTotalTimes")) {
            this.dryingTime = compoundTag.getIntArray("DryingTotalTimes").clone();
        }
        if (compoundTag.contains("FinishedDryings")) {
            this.finishedDrying = compoundTag.getIntArray("FinishedDryings").clone();
        }
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        ContainerHelper.saveAllItems(compoundTag, items, true, lookupProvider);
        compoundTag.putIntArray("DryingTimes", dryingProgress);
        compoundTag.putIntArray("DryingTotalTimes", dryingTime);
        compoundTag.putIntArray("FinishedDryings", finishedDrying);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, items, true, provider);
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

    public boolean hasUnfinishedItem() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items.get(i).isEmpty() && finishedDrying[i] <= 0) return true;
        }
        return false;
    }

    public ItemStack removeUnfinishedItem() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack removedStack = items.get(i);
            if (!removedStack.isEmpty() && finishedDrying[i] <= 0) {
                items.set(i, ItemStack.EMPTY);
                markUpdated();
                return removedStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!items.get(slot).isEmpty()) return;

        Optional<RecipeHolder<DryingRecipe>> recipe = getRecipeFor(stack);
        if (!recipe.isPresent()) return;

        placeItem(null, stack, recipe.get().value().time());
    }

    public boolean placeItem(@Nullable LivingEntity livingEntity, ItemStack stack, int time) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack rackItem = items.get(i);
            if (rackItem.isEmpty()) {
                dryingTime[i] = time;
                dryingProgress[i] = 0;
                finishedDrying[i] = 0;
                items.set(i, stack.consumeAndReturn(1, livingEntity));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), Context.of(livingEntity, getBlockState()));
                markUpdated();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return items.get(slot).isEmpty();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return canPlaceItem(slot, stack) && getRecipeFor(stack).isPresent();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
