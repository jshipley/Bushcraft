package com.jship.bushcraft.compat.jei.category;

import com.jship.bushcraft.compat.jei.BushcraftJeiPlugin;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

public class BushcraftJeiCoolingCategory extends AbstractRecipeCategory<CoolingRecipe> {

    public static final int width = 82;
    public static final int height = 40;

    public BushcraftJeiCoolingCategory(IGuiHelper guiHelper) {
        super(BushcraftJeiPlugin.COOLING_RECIPE, Component.translatable("gui.jei.category.bushcraft.cooling"), guiHelper.createDrawableItemLike(ModBlocks.CRUCIBLE.get()), width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().addFluidStack(recipe.input().getFluid(), recipe.input().getAmount());
        builder.addOutputSlot(61, 9).setOutputSlotBackground().addItemStack(recipe.result());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CoolingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(26, 9);
        builder.addAnimatedRecipeArrow(recipe.time()).setPosition(26, 9);
        builder
            .addText(Component.translatable("gui.jei.category.smelting.time.seconds", (int) (recipe.time() / 20)), getWidth() - 20, 10)
            .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
            .setTextAlignment(HorizontalAlignment.RIGHT)
            .setTextAlignment(VerticalAlignment.BOTTOM)
            .setColor(0xFF808080);
    }
    // @Override
    // public boolean isHandled(DryingRecipe recipe) {
    //     return !recipe.isSpecial();
    // }
}
