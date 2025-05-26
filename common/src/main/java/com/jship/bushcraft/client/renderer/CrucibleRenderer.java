package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.entity.CrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.architectury.hooks.fluid.FluidStackHooks;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.material.FluidState;

@Environment(EnvType.CLIENT)
public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {

    private final ItemRenderer itemRenderer;

    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CrucibleBlockEntity crucibleEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        val level = crucibleEntity.getLevel();
        val abovePos = crucibleEntity.getBlockPos().above();
        // isRedstoneConductor should test for full, solid blocks
        // so fluid won't render when under a block of cobblestone, but will render when
        // under a chest or stairs
        if (level.getBlockState(abovePos).isRedstoneConductor(level, abovePos)) {
            return;
        }

        val stack = crucibleEntity.getItemStorage(null).getStackInSlot(0);
        val pos = (int)crucibleEntity.getBlockPos().asLong();

        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.6f, 0.5f);
            if (stack.getItem() instanceof BlockItem) {
                poseStack.scale(1.2f, 1.2f, 1.2f);
            } else {
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.rotateAround(Axis.YP.rotationDegrees(45f), 0f, 0f, 0f);
            }
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, crucibleEntity.getLevel(), pos);
            poseStack.popPose();
        }

        val fluidStack = crucibleEntity.fluidStorage.getFluidInTank(0);
        if (fluidStack.isEmpty())
            return;

        TextureAtlasSprite stillSprite = FluidStackHooks.getStillTexture(fluidStack);
        if (stillSprite == null)
            return;

        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        int tintColor = FluidStackHooks.getColor(level, crucibleEntity.getBlockPos(), fluidState);
        // If the alpha is almost 0, set it higher
        // I'm doing this because the alpha for water was 0 on Fabric, and water was
        // invisible
        if ((tintColor & 0xFF000000) < 0x0F000000) {
            tintColor |= 0xCF000000;
        }

        float height = Math.min(1.0f,
                fluidStack.getAmount() / (float) crucibleEntity.fluidStorage.getTankCapacity(0))
                * (11.8f / 16f) + (4.01f / 16f);

        VertexConsumer builder = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));
        drawVertex(builder, poseStack, 3f / 16f, height, 3f / 16f, stillSprite.getU0(), stillSprite.getV0(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 3f / 16f, height, 13f / 16f, stillSprite.getU0(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 13f / 16f, height, 13f / 16f, stillSprite.getU1(), stillSprite.getV1(),
                packedLight, packedOverlay, tintColor);
        drawVertex(builder, poseStack, 13f / 16f, height, 3f / 16f, stillSprite.getU1(), stillSprite.getV0(),
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
