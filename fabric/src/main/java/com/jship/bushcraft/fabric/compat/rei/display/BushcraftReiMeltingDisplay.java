package com.jship.bushcraft.fabric.compat.rei.display;

import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.recipe.MeltingRecipe;
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

public class BushcraftReiMeltingDisplay extends BasicDisplay {

    RecipeHolder<MeltingRecipe> recipe;
    public int meltTime;

    public BushcraftReiMeltingDisplay(RecipeHolder<MeltingRecipe> recipe) {
        this(EntryIngredients.ofIngredients(List.of(recipe.value().ingredient())), Collections.singletonList(EntryIngredients.of(recipe.value().result())), recipe, recipe.value().time());
    }

    public BushcraftReiMeltingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
        this(input, output, RecipeManagerContext.getInstance().byId(tag, "location"), tag.getInt("meltTime"));
    }

    public BushcraftReiMeltingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, RecipeHolder<?> recipe, int meltTime) {
        super(input, output, Optional.ofNullable(recipe).map(RecipeHolder::id));
        this.meltTime = meltTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BushcraftReiPlugin.MELTING;
    }

    public static <R extends BushcraftReiMeltingDisplay> BasicDisplay.Serializer<R> serializer(BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
        return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
            tag.putDouble("meltTime", display.meltTime);
        });
    }
}
