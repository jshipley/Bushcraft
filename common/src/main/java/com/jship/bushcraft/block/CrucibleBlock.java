package com.jship.bushcraft.block;

import com.jship.bushcraft.block.entity.CrucibleBlockEntity;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.spiritapi.api.fluid.SpiritFluidUtil;
import com.mojang.serialization.MapCodec;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);
    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 0.1875, 0.25, 0.1875),
            Shapes.box(0, 0, 0.8125, 0.1875, 0.25, 1),
            Shapes.box(0.8125, 0, 0, 1, 0.25, 0.1875),
            Shapes.box(0.8125, 0, 0.8125, 1, 0.25, 1),
            Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375),
            Shapes.box(0.0625, 0.25, 0.0625, 0.1875, 1, 0.9375),
            Shapes.box(0.8125, 0.25, 0.0625, 0.9375, 1, 0.9375),
            Shapes.box(0.1875, 0.25, 0.0625, 0.8125, 1, 0.1875),
            Shapes.box(0.1875, 0.25, 0.8125, 0.8125, 1, 0.9375),
            Shapes.box(0, 0.625, 0, 0.0625, 0.75, 1),
            Shapes.box(0.9375, 0.625, 0, 1, 0.75, 1),
            Shapes.box(0.0625, 0.625, 0, 0.9375, 0.75, 0.0625),
            Shapes.box(0.0625, 0.625, 0.9375, 0.9375, 0.75, 1));

    public CrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
            BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide) {
            return createTickerHelper(blockEntityType, ModBlockEntities.CRUCIBLE.get(),
                    CrucibleBlockEntity::serverTick);
        }
        return null;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        val crucibleEntity = level.getBlockEntity(pos, ModBlockEntities.CRUCIBLE.get());
        if (crucibleEntity.isPresent()) {
            val fluidStorage = crucibleEntity.get().fluidStorage;
            if (!fluidStorage.getFluidInTank(0).isEmpty())
                return (int)Math.floor(1 + (fluidStorage.getFluidInTank(0).getAmount() / fluidStorage.getTankCapacity(0)) * 14);
        }
        return 0;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        val crucibleEntity = level.getBlockEntity(pos, ModBlockEntities.CRUCIBLE.get());
        if (crucibleEntity.isPresent()) {
            if (!crucibleEntity.get().getItemStorage(null).getStackInSlot(0).isEmpty()) {
                if (!level.isClientSide()) {
                    crucibleEntity.get().dropContents();
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        val crucibleEntity = level.getBlockEntity(pos, ModBlockEntities.CRUCIBLE.get());
        if (crucibleEntity.isPresent() && !player.isCrouching()) {
            if (SpiritFluidUtil.isFluidItem(stack)) {
                if (level.isClientSide())
                    return ItemInteractionResult.CONSUME;
                if (SpiritFluidUtil.fillItem(crucibleEntity.get().getFluidStorage(null), player, hand, false)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (crucibleEntity.get().getItemStorage(null).getStackInSlot(0).isEmpty()
                        && SpiritFluidUtil.drainItem(crucibleEntity.get().getFluidStorage(null), player, hand, false)) {
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                if (!level.isClientSide
                        && crucibleEntity.get().placeItem(player, stack)) {
                    SoundEvent sound = stack.getItem() instanceof BlockItem blockItem
                            ? blockItem.getBlock().defaultBlockState().getSoundType().getPlaceSound()
                            : SoundEvents.CAKE_ADD_CANDLE;
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            level.getBlockEntity(blockPos, ModBlockEntities.CRUCIBLE.get()).ifPresent(e -> e.dropContents());

            super.onRemove(blockState, level, blockPos, blockState2, bl);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

}
