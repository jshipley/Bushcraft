package com.jship.bushcraft.compat.jei.category;

import com.jship.bushcraft.compat.jei.BushcraftJeiPlugin;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.recipe.ChippingRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

public class BushcraftJeiChippingCategory extends AbstractRecipeCategory<ChippingRecipe> {

    public static final int width = 82;
    public static final int height = 40;

    public BushcraftJeiChippingCategory(IGuiHelper guiHelper) {
        super(BushcraftJeiPlugin.CHIPPING_RECIPE, Component.translatable("gui.jei.category.bushcraft.chipping"), guiHelper.createDrawableItemLike(ModBlocks.CHIPPER.get()), width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChippingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().addIngredients(recipe.getIngredients().getFirst());
        builder.addOutputSlot(61, 9).setOutputSlotBackground().addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ChippingRecipe recipe, IFocusGroup focuses) {
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
    // public boolean isHandled(ChippingRecipe recipe) {
    //     return !recipe.isSpecial();
    // }
}
