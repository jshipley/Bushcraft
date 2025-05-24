package com.jship.bushcraft.compat.emi.recipe;

import java.util.List;

import com.jship.bushcraft.compat.emi.BushcraftEmiPlugin;
import com.jship.bushcraft.recipe.WashingRecipe;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

public class EmiWashingRecipe extends BasicEmiRecipe {

    private final int time;

    public EmiWashingRecipe(ResourceLocation id, WashingRecipe recipe) {
        super(BushcraftEmiPlugin.WASHING_CATEGORY, BushcraftEmiPlugin.WASHING_ID, 94, 18);
        this.id = id;
        this.inputs.add(EmiStack.of(Fluids.WATER, FluidStack.bucketAmount() / (Platform.isFabric() ? 9 : 10)));
        this.inputs.add(EmiIngredient.of(recipe.getIngredients().getFirst()));
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        this.time = recipe.getCookingTime();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 44, 1).tooltipText(List.of(Component.translatable("emi.cooking.time", (int)(time / 20))));
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 44, 1, time * 50, true, false, false);

        // Adds an input slot on the left
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 18, 0);

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(outputs.get(0), 76, 0).recipeContext(this);
    }
}
