package com.jship.bushcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jship.bushcraft.init.ModTags.ModBlockTags;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Slf4j
@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {

    @Inject(method = "isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private static void injectIsSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.is(ModBlockTags.C_STICKY));
    }

    @Inject(method = "canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private static void injectCanStickToEachOther(BlockState state1, BlockState state2, CallbackInfoReturnable<Boolean> cir) {
        if (state1.is(ModBlockTags.C_STICKY) && state2.is(ModBlockTags.C_STICKY) && !state1.is(state2.getBlock())) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(state1.is(ModBlockTags.C_STICKY) || state2.is(ModBlockTags.C_STICKY));
        }
    }
}
