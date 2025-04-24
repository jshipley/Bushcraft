package com.jship.bushcraft.fabric.compat.rei.display;

import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.recipe.DryingRecipe;
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

public class BushcraftReiDryingDisplay extends BasicDisplay {

    RecipeHolder<DryingRecipe> recipe;
    public int dryTime;

    public BushcraftReiDryingDisplay(RecipeHolder<DryingRecipe> recipe) {
        this(EntryIngredients.ofIngredients(List.of(recipe.value().ingredient())), Collections.singletonList(EntryIngredients.of(recipe.value().result())), recipe, recipe.value().time());
    }

    public BushcraftReiDryingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
        this(input, output, RecipeManagerContext.getInstance().byId(tag, "location"), tag.getInt("dryTime"));
    }

    public BushcraftReiDryingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, RecipeHolder<?> recipe, int dryTime) {
        super(input, output, Optional.ofNullable(recipe).map(RecipeHolder::id));
        this.dryTime = dryTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BushcraftReiPlugin.DRYING;
    }

    public static <R extends BushcraftReiDryingDisplay> BasicDisplay.Serializer<R> serializer(BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
        return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
            tag.putDouble("dryTime", display.dryTime);
        });
    }
}
