package com.jship.bushcraft.fabric.compat.rei.display;

import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.recipe.ChippingRecipe;
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

public class BushcraftReiChippingDisplay extends BasicDisplay {

    RecipeHolder<ChippingRecipe> recipe;
    public int chipTime;

    public BushcraftReiChippingDisplay(RecipeHolder<ChippingRecipe> recipe) {
        this(List.of(EntryIngredients.ofIngredient(recipe.value().getIngredients().getFirst())), Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(null))), recipe, recipe.value().getCookingTime());
    }

    public BushcraftReiChippingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
        this(input, output, RecipeManagerContext.getInstance().byId(tag, "location"), tag.getInt("chipTime"));
    }

    public BushcraftReiChippingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, RecipeHolder<?> recipe, int chipTime) {
        super(input, output, Optional.ofNullable(recipe).map(RecipeHolder::id));
        this.chipTime = chipTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BushcraftReiPlugin.CHIPPING;
    }

    public static <R extends BushcraftReiChippingDisplay> BasicDisplay.Serializer<R> serializer(BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
        return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
            tag.putDouble("chipTime", display.chipTime);
        });
    }
}
