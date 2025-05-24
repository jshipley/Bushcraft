package com.jship.bushcraft.block;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.spiritapi.api.fluid.SpiritFluidUtil;
import com.mojang.serialization.MapCodec;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Slf4j
public class TreeTapBlock extends BaseEntityBlock {

    public static final MapCodec<TreeTapBlock> CODEC = simpleCodec(TreeTapBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
        Shapes.box(0, 0, 0, 1, 0.125, 1),
        Shapes.box(0.875, 0.125, 0, 1, 0.375, 1),
        Shapes.box(0, 0.125, 0, 0.125, 0.375, 1),
        Shapes.box(0.125, 0.125, 0.875, 0.875, 0.375, 1),
        Shapes.box(0.125, 0.125, 0, 0.875, 0.375, 0.125));
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TreeTapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<TreeTapBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { FACING });
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TreeTapBlockEntity(blockPos, blockState);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        val treeTapEntity = level.getBlockEntity(pos, ModBlockEntities.TREE_TAP.get());
        if (treeTapEntity.isPresent()) {
            treeTapEntity.get().randomTick(state, level, pos, random);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        val treeTapEntity = level.getBlockEntity(pos, ModBlockEntities.TREE_TAP.get());
        if (treeTapEntity.isPresent()) {
            val fluidStorage = treeTapEntity.get().fluidStorage;
            if (!fluidStorage.getFluidInTank(0).isEmpty())
                return (int)Math.floor(1 + (fluidStorage.getFluidInTank(0).getAmount() / fluidStorage.getTankCapacity(0)) * 14);
        }
        return 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level,
            BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        val tapEntity = level.getBlockEntity(blockPos, ModBlockEntities.TREE_TAP.get()).get();

        if (SpiritFluidUtil.isFluidItem(itemStack)) {
            if (level.isClientSide())
                return ItemInteractionResult.CONSUME;
            return SpiritFluidUtil.fillItem(tapEntity.fluidStorage, player, interactionHand, false)
                    ? ItemInteractionResult.SUCCESS
                    : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
