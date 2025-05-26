package com.jship.bushcraft.block.entity;

import com.jship.bushcraft.block.TreeTapBlock;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModFluids;
import com.jship.bushcraft.init.ModTags.ModBlockTags;
import com.jship.bushcraft.init.ModTags.ModFluidTags;
import com.jship.spiritapi.api.fluid.SpiritFluidStorage;
import com.jship.spiritapi.api.fluid.SpiritFluidStorageProvider;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Slf4j
public class TreeTapBlockEntity extends BlockEntity implements SpiritFluidStorageProvider {

    private static long SAP_AMOUNT = Platform.isFabric() ? FluidStack.bucketAmount() / 9 : FluidStack.bucketAmount() / 10;

    public final SpiritFluidStorage fluidStorage = SpiritFluidStorage.create(FluidStack.bucketAmount(),
            FluidStack.bucketAmount(), () -> markUpdated(), fluidStack -> fluidStack.getFluid().is(ModFluidTags.C_SAPS));

    public TreeTapBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.TREE_TAP.get(), blockPos, blockState);
    }

    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compoundTag, lookupProvider);
        fluidStorage.deserializeNbt(lookupProvider, compoundTag);
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        compoundTag.merge(fluidStorage.serializeNbt(lookupProvider));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag compoundTag = new CompoundTag();
        saveAdditional(compoundTag, lookupProvider);
        return compoundTag;
    }

    private void markUpdated() {
        this.setChanged();
        if (this.getLevel() != null)
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // if facing tree (spruce or birch)
        // then try to increase sap level in fluid storage
        val facingPos = pos.relative(state.getValue(TreeTapBlock.FACING));
        val facingBlockState = level.getBlockState(facingPos);

        if (!facingBlockState.is(ModBlockTags.PRODUCES_SAP))
            return;

        // search for a "healthy" tree that can produce sap
        // definition is a stack of at least 3 "produces_sap" logs connected to dirt and
        // adjacent to at least 4 leaf blocks 
        // this is intended to be fast and crude, especially since it's going to search again
        // every time this randomly ticks. I don't really care if the logs are a
        // mix of spruce and birch or if the leaves are from the same type of tree
        val down = scanTree(level, Direction.DOWN, facingPos);
        val up = scanTree(level, Direction.UP, facingPos.above());

        log.debug("Found tree with dirt beneath? {}, {} logs, and {} leaves", down.dirt(), up.logs() + down.logs(), up.leaves() + down.leaves());

        if (!down.dirt() || down.logs() + up.logs() < 3 || down.leaves() + up.leaves() < 4)
            return;
        
        var fluid = FluidStack.empty();
        if (facingBlockState.is(BlockTags.BIRCH_LOGS)) {
            fluid = FluidStack.create(ModFluids.BIRCH_SAP.get(), SAP_AMOUNT);
        }
        if (facingBlockState.is(BlockTags.SPRUCE_LOGS)) {
            fluid = FluidStack.create(ModFluids.SPRUCE_SAP.get(), SAP_AMOUNT);
        }
        val filled = fluidStorage.fill(fluid, false);
        log.debug("filled: {}, amount stored: {}", filled, fluidStorage.getFluidInTank(0).getAmount());
    }

    private record TreeResult(boolean dirt, int logs, int leaves) {
    }

    private TreeResult scanTree(ServerLevel level, Direction dir, BlockPos pos) {
        var logs = 0;
        var leaves = 0;
        var curPos = pos.mutable();

        while (level.getBlockState(curPos).is(ModBlockTags.PRODUCES_SAP)) {
            logs++;
            for (var horizontalDir : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH,
                    Direction.WEST }) {
                var searchBlockState = level.getBlockState(curPos.relative(horizontalDir));
                // for large (2x2) trees, if the adjacent block is a sap producing log, then
                // look for a leaf block on the other side
                if (searchBlockState.is(ModBlockTags.PRODUCES_SAP))
                    searchBlockState = level.getBlockState(curPos.relative(horizontalDir, 2));
                if (searchBlockState.is(BlockTags.LEAVES) && !searchBlockState.getValue(LeavesBlock.PERSISTENT))
                    leaves++;
            }
            curPos.set(curPos.relative(dir));
        }

        return new TreeResult(level.getBlockState(curPos).is(BlockTags.DIRT), logs, leaves);
    }

    @Override
    public SpiritFluidStorage getFluidStorage(Direction face) {
        return fluidStorage;
    }
}
