package com.jship.bushcraft.block.entity;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.init.ModTags.ModBlockTags;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.spiritapi.api.fluid.SpiritFluidStorage;
import com.jship.spiritapi.api.fluid.SpiritFluidStorageProvider;
import com.jship.spiritapi.api.item.SpiritItemStorage;
import com.jship.spiritapi.api.item.SpiritItemStorage.SlotConfig;
import com.jship.spiritapi.api.item.SpiritItemStorageProvider;

import dev.architectury.fluid.FluidStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class CrucibleBlockEntity extends BlockEntity implements SpiritFluidStorageProvider, SpiritItemStorageProvider {

    public int workProgress = 0;
    public int workTime = 0;
    public int workRate = 0;
    public ThermalMode workMode = ThermalMode.NEUTRAL;

    public final SpiritFluidStorage fluidStorage = SpiritFluidStorage.create(FluidStack.bucketAmount(),
            FluidStack.bucketAmount(),
            this::markUpdated);
    public final SpiritItemStorage itemStorage = SpiritItemStorage
            .create(ImmutableList.of(SlotConfig.SINGLE_ITEM_SLOT.withInsertFilter(stack -> fluidStorage.getFluidInTank(0).getAmount() < fluidStorage.getTankCapacity(0))),
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
        val stack = crucibleEntity.itemStorage.getStackInSlot(0);
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
                    crucibleEntity.itemStorage.extractItem(0, 1, false);
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
        if (fluidStack.isEmpty() || !crucibleEntity.itemStorage.getStackInSlot(0).isEmpty()) {
            crucibleEntity.resetProgress();
            return;
        }

        val recipeHolder = level.getRecipeManager().getAllRecipesFor(ModRecipes.COOLING.get()).stream()
                .filter(holder -> {
                    val recipeFluidStack = holder.value().input();
                    return fluidStack.getAmount() >= recipeFluidStack.getAmount()
                            && fluidStack.isFluidEqual(recipeFluidStack);
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
                    crucibleEntity.itemStorage.insertItem(0, stack, false);
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
        fluidStorage.deserializeNbt(lookupProvider, compoundTag);
        if (compoundTag.contains("Items"))
            this.itemStorage.deserializeNbt(lookupProvider, compoundTag);
        if (compoundTag.contains("WorkTime")) {
            this.workProgress = compoundTag.getInt("WorkTime");
        }
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        compoundTag.merge(fluidStorage.serializeNbt(lookupProvider));
        compoundTag.merge(itemStorage.serializeNbt(lookupProvider));
        compoundTag.putInt("WorkTime", workProgress);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.merge(fluidStorage.serializeNbt(lookupProvider));
        compoundTag.merge(this.itemStorage.serializeNbt(lookupProvider));
        return compoundTag;
    }

    public void dropContents() {
        var stacksDropped = false;
        val stack = this.itemStorage.getStackInSlot(0);
        if (!stack.isEmpty()) {
            val pos = this.getBlockPos();
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            this.itemStorage.extractItem(0, 1, false);
            stacksDropped = true;
        }

        if (stacksDropped)
            markUpdated();
    }

    public boolean placeItem(@Nullable LivingEntity livingEntity, ItemStack stack) {
        val updatedStack = itemStorage.insertItem(0, stack.copy(), true);
        if (updatedStack.getCount() < stack.getCount()) {
            resetProgress();
            val playerStack = livingEntity != null && livingEntity.hasInfiniteMaterials() ? stack.copy() : stack;
            itemStorage.insertItem(0, playerStack, false);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), Context.of(livingEntity, getBlockState()));
            return true;
        }

        return false;
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

    @Override
    public @Nullable SpiritItemStorage getItemStorage(Direction face) {
        return itemStorage;
    }

    @Override
    public @Nullable SpiritFluidStorage getFluidStorage(Direction face) {
        return fluidStorage;
    }
}
