package com.jship.bushcraft.fabric.compat.rei.category;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiCoolingDisplay;
import com.jship.bushcraft.init.ModBlocks;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.SimpleDisplayRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

public class BushcraftReiCoolingCategory implements DisplayCategory<BushcraftReiCoolingDisplay> {

    @Override
    public CategoryIdentifier<? extends BushcraftReiCoolingDisplay> getCategoryIdentifier() {
        return BushcraftReiPlugin.COOLING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.CRUCIBLE.get());
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.category.bushcraft.cooling");
    }

    @Override
    public int getDisplayHeight() {
        return 49;
    }

    @Override
    public List<Widget> setupDisplay(BushcraftReiCoolingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        int coolTime = display.coolTime;
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5), Component.translatable("gui.bushcraft.time_seconds", (int) (coolTime / 20))).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 8)).animationDurationTicks(coolTime));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entries(display.getInputEntries().get(0)).markInput());
        return widgets;
    }

    @Override
    public DisplayRenderer getDisplayRenderer(BushcraftReiCoolingDisplay display) {
        return SimpleDisplayRenderer.from(Collections.singletonList(display.getInputEntries().get(0)), display.getOutputEntries());
    }
}
