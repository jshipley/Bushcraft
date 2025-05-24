package com.jship.bushcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jship.bushcraft.init.ModBlocks;

import lombok.val;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin {

    @Inject(method = "isStickyForEntities()Z", at = @At("Return"), cancellable = true)
    private void injectIsStickyForEntities(CallbackInfoReturnable<Boolean> cir) {
        val movedState = ((PistonMovingBlockEntity)(Object)this).getMovedState();
        cir.setReturnValue(cir.getReturnValue() || movedState.is(ModBlocks.PITCH_BLOCK.get()));
    }
}
