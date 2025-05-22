package com.jship.bushcraft.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.architectury.hooks.fluid.FluidStackHooks;
import lombok.val;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluids;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class WasherFluidRenderer extends GeoRenderLayer<WasherGeoBlockEntity> {

    public WasherFluidRenderer(GeoRenderer<WasherGeoBlockEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, WasherGeoBlockEntity animatable, BakedGeoModel bakedModel,
            @Nullable RenderType renderType,
            MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
            int packedLight, int packedOverlay) {
        if (animatable.fluidStorage.getFluidInTank(0).isEmpty())
            return;

        val level = animatable.getLevel();

        // isRedstoneConductor should test for full, solid blocks
        // so fluid won't render when under a block of cobblestone, but will render when
        // under a chest or stairs

        val abovePos = animatable.getBlockPos().above();
        if (level.getBlockState(abovePos).isRedstoneConductor(level, abovePos)) {
            return;
        }

        // This block entity only accepts water, so we'll take a shortcut and only
        // render water
        val stillSprite = FluidStackHooks.getStillTexture(Fluids.WATER);
        var tintColor = FluidStackHooks.getColor(level, animatable.getBlockPos(), Fluids.WATER.defaultFluidState());
        // If the alpha is almost 0, set it higher
        // I'm doing this because the alpha for water was 0 on Fabric, and water was
        // invisible
        if ((tintColor & 0xFF000000) < 0x0F000000) {
            tintColor |= 0xCF000000;
        }

        val fluidCapacity = animatable.fluidStorage.getTankCapacity(0);
        val fluidAmount = animatable.fluidStorage.getFluidInTank(0).getAmount();
        float height = Math.min(1.0f, fluidAmount / (float) fluidCapacity) * (5.8f / 16f) + (10f / 16f);

        val builder = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(Fluids.WATER.defaultFluidState()));
        drawVertex(builder, poseStack, -7f / 16f, height, -7f / 16f, stillSprite.getU0(), stillSprite.getV0(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, -7f / 16f, height, 7f / 16f, stillSprite.getU0(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 7f / 16f, height, 7f / 16f, stillSprite.getU1(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 7f / 16f, height, -7f / 16f, stillSprite.getU1(), stillSprite.getV0(),
                packedLight, packedOverlay, tintColor);
    }

    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u,
            float v, int packedLight, int packedOverlay, int tintColor) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setUv(u, v)
                .setLight(packedLight)
                .setColor(tintColor)
                .setNormal(0, 1, 0);
    }

}
