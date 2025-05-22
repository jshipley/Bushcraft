package com.jship.bushcraft.block;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.mojang.serialization.MapCodec;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Slf4j
public class TreeTapBlock extends BaseEntityBlock {

    public static final MapCodec<TreeTapBlock> CODEC = simpleCodec(TreeTapBlock::new);

    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 6f / 16f, 1);
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

    // @Override
    // public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    //     if (!level.isClientSide) {
    //         return createTickerHelper(blockEntityType, ModBlockEntities.TREE_TAP.get(), DryingRackBlockEntity::dryTick);
    //     }
    //     return null;
    // }

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
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    // @Override
    // protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
    //     BlockEntity blockEntity = level.getBlockEntity(blockPos);
    //     if (blockEntity instanceof DryingRackBlockEntity dryingRackEntity && !player.isCrouching()) {
    //         // Eject finished stacks
    //         if (dryingRackEntity.hasFinishedStacks()) {
    //             if (!level.isClientSide) {
    //                 dryingRackEntity.dropFinishedStacks(level, blockPos, player);
    //                 // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
    //                 return ItemInteractionResult.SUCCESS;
    //             }
    //             return ItemInteractionResult.CONSUME;
    //         }

    //         // Otherwise try to add new stacks
    //         ItemStack handStack = player.getItemInHand(interactionHand);
    //         Optional<RecipeHolder<DryingRecipe>> recipe = dryingRackEntity.getRecipe(handStack);
    //         if (recipe.isPresent()) {
    //             if (!level.isClientSide && dryingRackEntity.placeItem(player, handStack, recipe.get().value().time())) {
    //                 SoundEvent sound = handStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState().getSoundType().getPlaceSound() : SoundEvents.SALMON_FLOP;
    //                 level.playSound(null, blockPos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    //                 // player.awardStat(ModStats.INTERACT_WITH_DRYING_RACK.get());
    //                 return ItemInteractionResult.SUCCESS;
    //             }

    //             return ItemInteractionResult.CONSUME;
    //         }
    //     }

    //     return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    // }
}
