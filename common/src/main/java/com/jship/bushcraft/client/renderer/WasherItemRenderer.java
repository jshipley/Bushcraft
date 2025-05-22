package com.jship.bushcraft.client.renderer;

import java.util.Objects;

import com.jship.bushcraft.block.WasherBlock;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

@Slf4j
public class WasherItemRenderer extends BlockAndItemGeoLayer<WasherGeoBlockEntity> {

    public WasherItemRenderer(GeoRenderer<WasherGeoBlockEntity> renderer) {
        super(renderer, (bone, animatable) -> {
            return Objects.equals(bone.getName(), "blockPos") ? animatable.getItem(0) : null;
        }, (bone, animatable) -> null);
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, WasherGeoBlockEntity animatable) {
        return ItemDisplayContext.GROUND;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, WasherGeoBlockEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        val bonePos = bone.getLocalPosition();

        val fluidAmount = animatable.fluidStorage.getFluidInTank(0).getAmount();
        val fluidCapacity = animatable.fluidStorage.getTankCapacity(0);

        poseStack.scale(0.5f, 0.5f, 0.5f);
        val moveItem = animatable.getBlockState().getValue(WasherBlock.LIT) && fluidAmount > 0;

        if (moveItem) {
            val age = animatable.getLevel().getGameTime() + partialTick;
            val bob = fluidAmount > 0 ? Math.sin(0.07f * age) / 10f : 0f;
               
            poseStack.translate(bonePos.x(), bonePos.y() + (0.7f * Math.min(fluidAmount, fluidCapacity) / fluidCapacity) + bob, bonePos.z());
            if (fluidAmount > 0)
                poseStack.mulPose(Axis.YP.rotation(age / 20f));
        } else {
            poseStack.translate(bonePos.x(), bonePos.y(), bonePos.z());
        }

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
