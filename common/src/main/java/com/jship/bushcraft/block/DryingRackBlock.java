package com.jship.bushcraft.block;

import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DryingRackBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<DryingRackBlock> CODEC = simpleCodec(DryingRackBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(Shapes.box(0, 0, 0, 0.25, 0.5, 0.25), Shapes.box(0.75, 0, 0, 1, 0.5, 0.25), Shapes.box(0, 0, 0.75, 0.25, 0.5, 1), Shapes.box(0.75, 0, 0.75, 1, 0.5, 1), Shapes.box(0, 0.5, 0, 1, 0.625, 1));
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public DryingRackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public MapCodec<DryingRackBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { WATERLOGGED, FACING });
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean waterlogged = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if ((Boolean) blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    protected BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    protected FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DryingRackBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide && !blockState.getValue(WATERLOGGED)) {
            return createTickerHelper(blockEntityType, ModBlockEntities.DRYING_RACK.get(), DryingRackBlockEntity::dryTick);
        }
        return null;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DryingRackBlockEntity dryingRackEntity && !player.isCrouching()) {
            if (dryingRackEntity.hasFinishedStacks()) {
                if (!level.isClientSide) {
                    dryingRackEntity.dropFinishedStacks(level, blockPos, player);
                    // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            } else if (dryingRackEntity.hasUnfinishedItem() && player.getMainHandItem().isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack removedItem = dryingRackEntity.removeUnfinishedItem();
                    if (!removedItem.isEmpty() && !player.hasInfiniteMaterials())
                        player.addItem(removedItem);
                    // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DryingRackBlockEntity dryingRackEntity && !player.isCrouching()) {
            // Eject finished stacks
            if (dryingRackEntity.hasFinishedStacks()) {
                if (!level.isClientSide) {
                    dryingRackEntity.dropFinishedStacks(level, blockPos, player);
                    // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.CONSUME;
            }

            // Otherwise try to add new stacks
            ItemStack handStack = player.getItemInHand(interactionHand);
            Optional<RecipeHolder<DryingRecipe>> recipe = dryingRackEntity.getRecipe(handStack);
            if (recipe.isPresent()) {
                if (!level.isClientSide && dryingRackEntity.placeItem(player, handStack, recipe.get().value().time())) {
                    SoundEvent sound = handStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState().getSoundType().getPlaceSound() : SoundEvents.SALMON_FLOP;
                    level.playSound(null, blockPos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
                    return ItemInteractionResult.SUCCESS;
                }

                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRackEntity) {
                Containers.dropContents(level, blockPos, dryingRackEntity.getItems());
            }

            super.onRemove(blockState, level, blockPos, blockState2, bl);
        }
    }
}
