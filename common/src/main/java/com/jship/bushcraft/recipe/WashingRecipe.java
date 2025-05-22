package com.jship.bushcraft.recipe;

import com.jship.bushcraft.Bushcraft.ModBlocks;
import com.jship.bushcraft.Bushcraft.ModRecipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class WashingRecipe extends AbstractCookingRecipe {
    public WashingRecipe(String group, CookingBookCategory category, Ingredient input, ItemStack result, float experience, int cookTime) {
        super(ModRecipes.WASHING.get(), group, category, input, result, experience, cookTime);
    }

    public ItemStack getToastSymbol() {
      return new ItemStack(ModBlocks.WASHER.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipes.WASHING_SERIALIZER.get();
   }

   public boolean isSpecial() {
    return true;
   }
}
