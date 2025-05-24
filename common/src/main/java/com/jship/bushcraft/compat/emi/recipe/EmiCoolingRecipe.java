package com.jship.bushcraft.compat.emi.recipe;

import com.jship.bushcraft.compat.emi.BushcraftEmiPlugin;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EmiCoolingRecipe extends BasicEmiRecipe {

    private final int time;

    public EmiCoolingRecipe(ResourceLocation id, CoolingRecipe recipe) {
        super(BushcraftEmiPlugin.COOLING_CATEGORY, BushcraftEmiPlugin.COOLING_ID, 76, 18);
        this.id = id;
        this.inputs.add(EmiStack.of(recipe.input().getFluid(), recipe.input().getAmount()));
        this.outputs.add(EmiStack.of(recipe.result()));
        this.time = recipe.time();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1).tooltipText(List.of(Component.translatable("emi.cooking.time", (int)(time / 20))));
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 26, 1, time * 50, true, false, false);

        // Adds an input slot on the left
        widgets.addSlot(inputs.get(0), 0, 0);

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(outputs.get(0), 58, 0).recipeContext(this);
    }
}
