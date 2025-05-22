package com.jship.bushcraft.block.entity;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.block.HandPumpBlock;
import com.jship.spiritapi.api.fluid.SpiritFluidStorage;
import com.jship.spiritapi.api.fluid.SpiritFluidUtil;

import dev.architectury.fluid.FluidStack;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@Slf4j
public class HandPumpGeoBlockEntity extends BlockEntity implements GeoBlockEntity {

    public static final String ANIM_NAME = "pump_animation";
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("animation.model.idle");
    public static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenPlay("animation.model.working");

    public static final double SECONDS_PER_PUMP = 1.0d;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private long pumpTime = -1;
    private final Queue<BlockPos> foundFluidBlocks = new ArrayDeque<>();

    public HandPumpGeoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.HAND_PUMP.get(), pos, blockState);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, ANIM_NAME, (anim) -> anim.setAndContinue(IDLE_ANIM))
                .triggerableAnim("pump", WORKING_ANIM).setAnimationSpeed(1.0d / SECONDS_PER_PUMP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void searchForFluids(ServerLevel level, BlockPos pos) {
        val stopwatch = Stopwatch.createStarted();

        foundFluidBlocks.clear();
        BlockPos.withinManhattanStream(pos, 10, 10, 10).filter(p -> {
            return p.getY() < pos.getY() && level.getBlockState(p).getFluidState().isSource();
        }).forEach(p -> {
            foundFluidBlocks.add(p.immutable());
        });
        stopwatch.stop();
        log.debug("Found {} fluid blocks to pump in {}ms", foundFluidBlocks.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public InteractionResult tryPumpFluid(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide() || level.getGameTime() < pumpTime + (int)(SECONDS_PER_PUMP * 20)) {
            return InteractionResult.CONSUME;
        }

        if (foundFluidBlocks.isEmpty())
            searchForFluids((ServerLevel) level, pos);

        var fluidPumped = false;
        while (!fluidPumped && !foundFluidBlocks.isEmpty()) {
            val fluidPos = foundFluidBlocks.remove();
            val fluidBlockState = level.getBlockState(fluidPos);
            val fluidState = fluidBlockState.getFluidState();

            // the pump doesn't have an internal buffer
            // create fluid storage to make it easier to use the Spirit fluid api
            val fluidStorage = SpiritFluidStorage.create(FluidStack.bucketAmount(), FluidStack.bucketAmount(), () -> {
            });

            if (fluidBlockState.getBlock() instanceof BucketPickup bucketPickup) {
                val facing = state.getValue(HandPumpBlock.FACING);
                val facingPos = pos.relative(facing);
                val facingState = level.getBlockState(facingPos);
                var bucket = ItemStack.EMPTY;
                // try to simulate an insert / place before picking up fluid
                if (SpiritFluidUtil.fillBlockPos(fluidStorage, level, facingPos, facing, true) > 0) {
                    bucket = bucketPickup.pickupBlock(null, level, fluidPos, fluidBlockState);
                    if (!bucket.isEmpty()) {
                        SpiritFluidUtil.fillBlockPos(fluidStorage, level, facingPos, facing, false);
                        fluidPumped = true;
                    }
                } else if (facingState.canBeReplaced(fluidState.getType())) {
                    bucket = bucketPickup.pickupBlock(null, level, fluidPos, fluidBlockState);
                    if (!bucket.isEmpty()) {
                        val bucketItem = (BucketItem) bucket.getItem();
                        bucketItem.emptyContents(null, level, facingPos, null);
                        fluidPumped = true;
                    }
                }
                if (fluidPumped) {
                    pumpTime = level.getGameTime();
                    // probably not necessary to stop the animation because the
                    // animation should have ended by now
                    stopTriggeredAnim(ANIM_NAME, "pump");
                    triggerAnim(HandPumpGeoBlockEntity.ANIM_NAME, "pump");
                    player.causeFoodExhaustion(1.0f);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // No fluid to pump, nowhere to put pumped fluid, or still waiting for previous pump action to finish
        return InteractionResult.CONSUME;
    }
}
