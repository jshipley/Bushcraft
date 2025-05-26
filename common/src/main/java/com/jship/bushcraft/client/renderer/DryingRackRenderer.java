package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.DryingRackBlock;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    private static final float SIZE = 0.375F;
    private static final float ITEM_HEIGHT = 0.64F;
    private static final float BLOCK_ITEM_HEIGHT = 0.72F;
    private static final float ITEM_RADIUS = -0.25F;
    private final ItemRenderer itemRenderer;

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DryingRackBlockEntity dryingRackEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Direction direction = dryingRackEntity.getBlockState().getValue(DryingRackBlock.FACING);
        int pos = (int) dryingRackEntity.getBlockPos().asLong();

        for (int index = 0; index < dryingRackEntity.getItemStorage(null).getSlots(); index++) {
            ItemStack stack = dryingRackEntity.getItemStorage(null).getStackInSlot(index);
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.5F, stack.getItem() instanceof BlockItem ? BLOCK_ITEM_HEIGHT : ITEM_HEIGHT, 0.5F);
                Direction direction2 = Direction.from2DDataValue((index + direction.get2DDataValue()) % 4);
                float g = -direction2.toYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(g));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.translate(ITEM_RADIUS, ITEM_RADIUS, 0.0F);
                poseStack.scale(SIZE, SIZE, SIZE);
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, multiBufferSource, dryingRackEntity.getLevel(), pos + index);
                poseStack.popPose();
            }
        }
    }
}
