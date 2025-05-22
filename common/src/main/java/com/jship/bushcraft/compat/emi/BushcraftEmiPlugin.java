package com.jship.bushcraft.compat.emi;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.Bushcraft.ModRecipes;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.compat.emi.recipe.EmiDryingRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class BushcraftEmiPlugin implements EmiPlugin {

    public static final EmiStack DRYING_RACK = EmiStack.of(Bushcraft.ModBlocks.DRYING_RACK.get());
    public static final ResourceLocation DRYING_ID = ModRecipes.DRYING.getId();
    public static final EmiRecipeCategory DRYING_CATEGORY = new EmiRecipeCategory(DRYING_ID, DRYING_RACK, DRYING_RACK);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(DRYING_CATEGORY);
        registry.addWorkstation(DRYING_CATEGORY, DRYING_RACK);

        RecipeManager recipeManager = registry.getRecipeManager();

        for (RecipeHolder<DryingRecipe> recipe : recipeManager.getAllRecipesFor(Bushcraft.ModRecipes.DRYING.get())) {
            registry.addRecipe(new EmiDryingRecipe(recipe.id(), recipe.value()));
        }
    }
}
