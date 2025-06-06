package com.jship.bushcraft.client.renderer;

import java.util.Objects;

import com.jship.bushcraft.block.entity.ChipperGeoBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

@Slf4j
public class ChipperItemRenderer extends BlockAndItemGeoLayer<ChipperGeoBlockEntity> {

    public ChipperItemRenderer(GeoRenderer<ChipperGeoBlockEntity> renderer) {
        super(renderer, (bone, animatable) -> {
            return Objects.equals(bone.getName(), "blockPos") ? animatable.getItemStorage(null).getStackInSlot(0) : null;
        }, (bone, animatable) -> null);
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ChipperGeoBlockEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        val bonePos = bone.getLocalPosition();

        poseStack.scale(0.5f, 0.5f, 0.5f);
        val progress = animatable.assembleTotalTime() > 0 ? (float)animatable.assembleTime() / (float)animatable.assembleTotalTime() : 0f; 
        poseStack.translate(0, bonePos.y() + 0.5f - progress, 0);

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
