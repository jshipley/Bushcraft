package com.jship.bushcraft.fabric.compat.rei.display;

import com.jship.bushcraft.fabric.compat.rei.BushcraftReiPlugin;
import com.jship.bushcraft.recipe.WashingRecipe;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;

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
import net.minecraft.world.level.material.Fluids;

public class BushcraftReiWashingDisplay extends BasicDisplay {

    RecipeHolder<WashingRecipe> recipe;
    public int washTime;

    public BushcraftReiWashingDisplay(RecipeHolder<WashingRecipe> recipe) {
        this(
                List.of(
                        EntryIngredients.of(Fluids.WATER, FluidStack.bucketAmount() / (Platform.isFabric() ? 9 : 10)),
                        EntryIngredients.ofIngredient(recipe.value().getIngredients().getFirst())),
                Collections.singletonList(
                        EntryIngredients.of(recipe.value().getResultItem(null))),
                recipe, recipe.value().getCookingTime());
    }

    public BushcraftReiWashingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
        this(input, output, RecipeManagerContext.getInstance().byId(tag, "location"), tag.getInt("washTime"));
    }

    public BushcraftReiWashingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, RecipeHolder<?> recipe,
            int washTime) {
        super(input, output, Optional.ofNullable(recipe).map(RecipeHolder::id));
        this.washTime = washTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BushcraftReiPlugin.WASHING;
    }

    public static <R extends BushcraftReiWashingDisplay> BasicDisplay.Serializer<R> serializer(
            BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
        return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
            tag.putDouble("washTime", display.washTime);
        });
    }
}
