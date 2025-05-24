package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.architectury.hooks.fluid.FluidStackHooks;
import lombok.val;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class TreeTapRenderer implements BlockEntityRenderer<TreeTapBlockEntity> {

    public TreeTapRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TreeTapBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        Level level = blockEntity.getLevel();
        BlockPos abovePos = blockEntity.getBlockPos().above();
        // isRedstoneConductor should test for full, solid blocks
        // so fluid won't render when under a block of cobblestone, but will render when
        // under a chest or stairs
        if (level.getBlockState(abovePos).isRedstoneConductor(level, abovePos)) {
            return;
        }

        val fluidStack = blockEntity.fluidStorage.getFluidInTank(0);
        if (fluidStack.isEmpty())
            return;

        TextureAtlasSprite stillSprite = FluidStackHooks.getStillTexture(fluidStack);
        if (stillSprite == null)
            return;

        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        int tintColor = FluidStackHooks.getColor(level, blockEntity.getBlockPos(), fluidState);
        // If the alpha is almost 0, set it higher
        // I'm doing this because the alpha for water was 0 on Fabric, and water was
        // invisible
        if ((tintColor & 0xFF000000) < 0x0F000000) {
            tintColor |= 0xCF000000;
        }

        float height = Math.min(1.0f,
                fluidStack.getAmount() / (float) blockEntity.fluidStorage.getTankCapacity(0))
                * (3.8f / 16f) + (2.01f / 16f);

        VertexConsumer builder = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));
        drawVertex(builder, poseStack, 2f / 16f, height, 2f / 16f, stillSprite.getU0(), stillSprite.getV0(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 2f / 16f, height, 14f / 16f, stillSprite.getU0(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 14f / 16f, height, 14f / 16f, stillSprite.getU1(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 14f / 16f, height, 2f / 16f, stillSprite.getU1(), stillSprite.getV0(),
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
