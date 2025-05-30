package com.jship.bushcraft.block;

import static com.jship.bushcraft.block.entity.ChipperGeoBlockEntity.INPUT_SLOT;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.block.entity.ChipperGeoBlockEntity;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModSounds;
import com.mojang.serialization.MapCodec;

import dev.architectury.registry.menu.MenuRegistry;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Slf4j
public class ChipperBlock extends BaseEntityBlock {
    public static final MapCodec<ChipperBlock> CODEC = simpleCodec(ChipperBlock::new);
    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 1, 0.625, 1),
            Shapes.box(0.1875, 0.5625, 0.125, 0.8125, 0.875, 0.1875),
            Shapes.box(0.1875, 0.5625, 0.8125, 0.8125, 0.875, 0.875),
            Shapes.box(0.125, 0.625, 0.125, 0.1875, 0.875, 0.875),
            Shapes.box(0.8125, 0.625, 0.125, 0.875, 0.875, 0.875));

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ChipperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { FACING, LIT });
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChipperGeoBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide())
            return;

        if (entity instanceof ItemEntity itemEntity) {
            val x = itemEntity.position().x % 1;
            val y = itemEntity.position().y % 1;
            val z = itemEntity.position().z % 1;
            if (x >= 0.1875f && x <= 0.8125f && y >= 0.625f && y < 0.875f && z >= 0.1875f && z <= 0.8125f) {
                val blockEntity = level.getBlockEntity(pos, ModBlockEntities.CHIPPER.get());
                val stack = itemEntity.getItem();
                if (blockEntity.isPresent() && !stack.isEmpty()
                        && blockEntity.get().itemStorage.isItemValid(INPUT_SLOT, stack)) {
                    val stackRemainder = blockEntity.get().itemStorage.insertItem(INPUT_SLOT, stack, false);
                    if (stackRemainder.isEmpty()) {
                        itemEntity.setItem(ItemStack.EMPTY);
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(stackRemainder);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.openContainer(level, pos, player);
            return InteractionResult.CONSUME;
        }
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        val blockEntity = level.getBlockEntity(pos, ModBlockEntities.CHIPPER.get()).orElseThrow();
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, blockEntity);
        }
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            val blockEntity = level.getBlockEntity(pos, ModBlockEntities.CHIPPER.get()).orElseThrow();
            if (level instanceof ServerLevel) {
                val itemStorage = blockEntity.itemStorage;
                for (int i = 0; i < itemStorage.getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                            itemStorage.getStackInSlot(i));
                }
                blockEntity.getRecipesToAwardAndPopExperience((ServerLevel) level, Vec3.atCenterOf(pos));
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
            level.updateNeighbourForOutputSignal(pos, this);
        } else {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        // TODO move most of this into a util class
        val blockEntity = level.getBlockEntity(pos, ModBlockEntities.CHIPPER.get()).orElseThrow();
        var sum = 0;
        var total = 0;
        val itemStorage = blockEntity.itemStorage;
        for (int i = 0; i < itemStorage.getSlots(); i++) {
            total += itemStorage.getSlotLimit(i);
            var stack = itemStorage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                sum += stack.getCount() * (64 / stack.getMaxStackSize());
            }
        }
        return sum > 0 ? (int) Math.floor(1 + ((double) total / (double) sum) * 14) : 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null
                : createTickerHelper(blockEntityType, ModBlockEntities.CHIPPER.get(),
                        ChipperGeoBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        val blockEntity = level.getBlockEntity(pos, ModBlockEntities.CHIPPER.get()).orElseThrow();
        if ((Boolean) state.getValue(LIT)) {
            blockEntity.triggerAnim(ChipperGeoBlockEntity.ANIM_NAME, "chip");

            double d = (double) pos.getX() + 0.5;
            double e = (double) pos.getY();
            double f = (double) pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            val inputStack = blockEntity.itemStorage.getStackInSlot(INPUT_SLOT);
            if (!inputStack.isEmpty()) {
                if (random.nextDouble() < 0.1) {
                    level.playLocalSound(d, e, f, ModSounds.CHIPPER_WORKING.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
                }
                level.addParticle(ParticleTypes.WHITE_SMOKE, d + random.nextDouble() * 0.6 - 0.3, e + 0.65f, f + random.nextDouble() * 0.6 - 0.3, 0.0, 0.0, 0.0);
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
            ((ChipperGeoBlockEntity) blockEntity).stopTriggeredAnim(ChipperGeoBlockEntity.ANIM_NAME, "chip");
        }
    }

    @Override
    protected MapCodec<ChipperBlock> codec() {
        return CODEC;
    }
}
