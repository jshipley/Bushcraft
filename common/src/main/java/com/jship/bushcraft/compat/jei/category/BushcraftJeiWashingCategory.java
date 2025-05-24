package com.jship.bushcraft.compat.jei.category;

import com.jship.bushcraft.compat.jei.BushcraftJeiPlugin;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;

public class BushcraftJeiWashingCategory extends AbstractRecipeCategory<WashingRecipe> {

    public static final int width = 100;
    public static final int height = 40;

    public BushcraftJeiWashingCategory(IGuiHelper guiHelper) {
        super(BushcraftJeiPlugin.WASHING_RECIPE, Component.translatable("gui.jei.category.bushcraft.washing"), guiHelper.createDrawableItemLike(ModBlocks.WASHER.get()), width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WashingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().addFluidStack(Fluids.WATER, FluidStack.bucketAmount() / (Platform.isFabric() ? 9 : 10));
        builder.addInputSlot(19, 9).setStandardSlotBackground().addIngredients(recipe.getIngredients().getFirst());
        builder.addOutputSlot(79, 9).setOutputSlotBackground().addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, WashingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(44, 9);
        builder.addAnimatedRecipeArrow(recipe.getCookingTime()).setPosition(44, 9);
        builder
            .addText(Component.translatable("gui.jei.category.smelting.time.seconds", (int) (recipe.getCookingTime() / 20)), getWidth() - 20, 10)
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
