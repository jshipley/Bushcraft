package com.jship.bushcraft.fabric.compat.rei.display;

import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.recipe.CoolingRecipe;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.RecipeManagerContext;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BushcraftReiCoolingDisplay extends BasicDisplay {

    RecipeHolder<CoolingRecipe> recipe;
    public int coolTime;

    public BushcraftReiCoolingDisplay(RecipeHolder<CoolingRecipe> recipe) {
        this(List.of(EntryIngredients.of(recipe.value().input())), Collections.singletonList(EntryIngredients.of(recipe.value().result())), recipe, recipe.value().time());
    }

    public BushcraftReiCoolingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
        this(input, output, RecipeManagerContext.getInstance().byId(tag, "location"), tag.getInt("coolTime"));
    }

    public BushcraftReiCoolingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, RecipeHolder<?> recipe, int coolTime) {
        super(input, output, Optional.ofNullable(recipe).map(RecipeHolder::id));
        this.coolTime = coolTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BushcraftReiPlugin.COOLING;
    }

    public static <R extends BushcraftReiCoolingDisplay> BasicDisplay.Serializer<R> serializer(BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
        return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
            tag.putDouble("coolTime", display.coolTime);
        });
    }
}
