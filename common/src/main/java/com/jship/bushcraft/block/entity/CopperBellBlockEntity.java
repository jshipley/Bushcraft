package com.jship.bushcraft.block.entity;

import com.jship.bushcraft.init.ModBlockEntities;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

@Slf4j
public class CopperBellBlockEntity extends BlockEntity {

    public CopperBellBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COPPER_BELL.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
            CopperBellBlockEntity copperBellEntity) {
        if (level.isClientSide() || level.getGameTime() % 10 > 0 || level.getRandom().nextFloat() > 0.4f) {
            return;
        }

        if (level.getEntities((Entity) null, AABB.ofSize(pos.getCenter(), 8, 8, 4), entity -> entity instanceof Creeper).size() > 0)
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 4.0f, 1.8f);
    }
}
