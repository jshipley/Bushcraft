package com.jship.bushcraft.client.screen;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.menu.ChipperMenu;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ChipperScreen extends AbstractContainerScreen<ChipperMenu> {
    public static final ResourceLocation TEXTURE = Bushcraft.id("textures/gui/container/chipper.png");
    public static final ResourceLocation LIT_PROGRESS_TEXTURE = ResourceLocation
            .withDefaultNamespace("container/furnace/lit_progress");
    public static final ResourceLocation ASSEMBLE_PROGRESS_TEXTURE = ResourceLocation
            .withDefaultNamespace("container/furnace/burn_progress");

    public ChipperScreen(ChipperMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, (width - imageWidth) / 2, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight);
        if (this.menu.isLit()) {
            int pct = Mth.ceil(this.menu.litProgress() * 13f) + 1;
            guiGraphics.blitSprite(LIT_PROGRESS_TEXTURE, 14, 14, 0, 14 - pct, this.leftPos + 25, this.topPos + 25 + 14 - pct, 14, pct);
        }
        int pct = Mth.ceil(this.menu.assembleProgress() * 24f);
        guiGraphics.blitSprite(ASSEMBLE_PROGRESS_TEXTURE, 24, 16, 0, 0, this.leftPos + 79, this.topPos + 34, pct, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
