package com.jship.bushcraft.block;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
import com.jship.spiritapi.api.fluid.SpiritFluidUtil;
import com.mojang.serialization.MapCodec;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class WasherBlock extends AbstractFurnaceBlock {

    public static final MapCodec<WasherBlock> CODEC = simpleCodec(WasherBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public WasherBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { FACING, LIT });
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WasherGeoBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (SpiritFluidUtil.isFluidItem(stack)) {
            if (level.isClientSide()) return ItemInteractionResult.CONSUME;

            val blockEntity = level.getBlockEntity(pos, ModBlockEntities.WASHER.get());
            if (blockEntity.isPresent()) {
                return SpiritFluidUtil.drainItem(blockEntity.get().fluidStorage, player, hand, false)
                        ? ItemInteractionResult.SUCCESS
                        : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }       
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        val blockEntity = level.getBlockEntity(pos, ModBlockEntities.WASHER.get());
        if (blockEntity.isPresent()) {
            player.openMenu((MenuProvider) blockEntity.get());
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.WASHER.get(), WasherGeoBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        val blockEntity = level.getBlockEntity(pos);
        if ((Boolean) state.getValue(LIT)) {
            ((WasherGeoBlockEntity)blockEntity).triggerAnim(WasherGeoBlockEntity.ANIM_NAME, "wash");

            double d = (double) pos.getX() + 0.5;
            double e = (double) pos.getY();
            double f = (double) pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            if (random.nextDouble() < 0.05f) {
                level.playLocalSound(d, e, f, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = (Direction) state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double g = 0.52;
            double h = random.nextDouble() * 0.6 - 0.3;
            double i = axis == Axis.X ? (double) direction.getStepX() * g : h;
            double j = random.nextDouble() * 6.0 / 16.0;
            double k = axis == Axis.Z ? (double) direction.getStepZ() * g : h;
            level.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0);
        } else {
            ((WasherGeoBlockEntity)blockEntity).stopTriggeredAnim(WasherGeoBlockEntity.ANIM_NAME, "wash");
        }
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    // @Override
    // protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    //     val pump = level.getBlockEntity(pos, ModBlockEntities.HAND_PUMP.get());
    //     if (pump.isPresent()) {
    //         return pump.get().tryPumpFluid(state, level, pos, player);
    //     }
    //     return InteractionResult.PASS;
    // }
}
