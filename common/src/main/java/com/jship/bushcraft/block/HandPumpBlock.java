package com.jship.bushcraft.block;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.block.entity.HandPumpGeoBlockEntity;
import com.jship.bushcraft.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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

public class HandPumpBlock extends BaseEntityBlock {

    public static final MapCodec<HandPumpBlock> CODEC = simpleCodec(HandPumpBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
        Direction.NORTH,
        Shapes.or(
            Shapes.box(0.471875, 0.375, 0.47099875, 0.534375, 1, 0.53349875),
	        Shapes.box(0.40625, 0.46875, 0, 0.59375, 0.65625, 0.0625), // 1
	        Shapes.box(0.375, 0.125, 0.375, 0.625, 0.75, 0.625),
	        Shapes.box(0, 0, 0, 1, 0.125, 1),
	        Shapes.box(0.4375, 0.5, 0.0625, 0.5625, 0.625, 0.375), // 2
	        Shapes.box(0.46875, 0.625, 0.5625, 0.53125, 0.96875, 0.6875), // 3
	        Shapes.box(0.4375, 0.625, 0.4375, 0.5625, 1.1875, 1)), // 4
        Direction.EAST,
        Shapes.or(
            Shapes.box(0.471875, 0.375, 0.47099875, 0.534375, 1, 0.53349875),
	        Shapes.box(0.9375, 0.46875, 0.40625, 1, 0.65625, 0.59375), // 1
	        Shapes.box(0.375, 0.125, 0.375, 0.625, 0.75, 0.625),
	        Shapes.box(0, 0, 0, 1, 0.125, 1),
	        Shapes.box(0.625, 0.5, 0.4375, 0.9375, 0.625, 0.5625), // 2
	        Shapes.box(0.3125, 0.625, 0.46875, 0.4375, 0.96875, 0.53125), // 3
	        Shapes.box(0, 0.625, 0.4375, 0.5625, 1.1875, 0.5625)), // 4
        Direction.SOUTH,
        Shapes.or(
            Shapes.box(0.471875, 0.375, 0.47099875, 0.534375, 1, 0.53349875),
	        Shapes.box(0.40625, 0.46875, 0.9375, 0.59375, 0.65625, 1), // 1
	        Shapes.box(0.375, 0.125, 0.375, 0.625, 0.75, 0.625),
	        Shapes.box(0, 0, 0, 1, 0.125, 1),
	        Shapes.box(0.4375, 0.5, 0.625, 0.5625, 0.625, 0.9375), // 2
	        Shapes.box(0.46875, 0.625, 0.3125, 0.53125, 0.96875, 0.4375), // 3
	        Shapes.box(0.4375, 0.625, 0, 0.5625, 1.1875, 0.5625)), // 4
        Direction.WEST,
        Shapes.or(
            Shapes.box(0.471875, 0.375, 0.47099875, 0.534375, 1, 0.53349875),
	        Shapes.box(0, 0.46875, 0.40625, 0.0625, 0.65625, 0.59375), // 1
	        Shapes.box(0.375, 0.125, 0.375, 0.625, 0.75, 0.625),
	        Shapes.box(0, 0, 0, 1, 0.125, 1),
	        Shapes.box(0.0625, 0.5, 0.4375, 0.375, 0.625, 0.5625), // 2
	        Shapes.box(0.5625, 0.625, 0.46875, 0.6875, 0.96875, 0.53125), // 3
	        Shapes.box(0.4375, 0.625, 0.4375, 1, 1.1875, 0.5625))); // 4

    public HandPumpBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { FACING });
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HandPumpGeoBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        val pump = level.getBlockEntity(pos, ModBlockEntities.HAND_PUMP.get());
        if (pump.isPresent()) {
            return pump.get().tryPumpFluid(state, level, pos, player);
        }
        return InteractionResult.PASS;
    }
}
