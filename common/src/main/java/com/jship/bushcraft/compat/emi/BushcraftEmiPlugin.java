package com.jship.bushcraft.compat.emi;

import com.jship.bushcraft.compat.emi.recipe.EmiCoolingRecipe;
import com.jship.bushcraft.compat.emi.recipe.EmiDryingRecipe;
import com.jship.bushcraft.compat.emi.recipe.EmiMeltingRecipe;
import com.jship.bushcraft.compat.emi.recipe.EmiWashingRecipe;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModRecipes;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class BushcraftEmiPlugin implements EmiPlugin {

    public static final EmiStack CRUCIBLE = EmiStack.of(ModBlocks.CRUCIBLE.get());
    public static final ResourceLocation COOLING_ID = ModRecipes.COOLING.getId();
    public static final EmiRecipeCategory COOLING_CATEGORY = new EmiRecipeCategory(COOLING_ID, CRUCIBLE, CRUCIBLE);
    public static final ResourceLocation MELTING_ID = ModRecipes.MELTING.getId();
    public static final EmiRecipeCategory MELTING_CATEGORY = new EmiRecipeCategory(MELTING_ID, CRUCIBLE, CRUCIBLE);
    
    public static final EmiStack DRYING_RACK = EmiStack.of(ModBlocks.DRYING_RACK.get());
    public static final ResourceLocation DRYING_ID = ModRecipes.DRYING.getId();
    public static final EmiRecipeCategory DRYING_CATEGORY = new EmiRecipeCategory(DRYING_ID, DRYING_RACK, DRYING_RACK);

    public static final EmiStack WASHER = EmiStack.of(ModBlocks.WASHER.get());
    public static final ResourceLocation WASHING_ID = ModRecipes.WASHING.getId();
    public static final EmiRecipeCategory WASHING_CATEGORY = new EmiRecipeCategory(WASHING_ID, WASHER, WASHER);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(COOLING_CATEGORY);
        registry.addWorkstation(COOLING_CATEGORY, CRUCIBLE);
        registry.addCategory(MELTING_CATEGORY);
        registry.addWorkstation(MELTING_CATEGORY, CRUCIBLE);
        registry.addCategory(DRYING_CATEGORY);
        registry.addWorkstation(DRYING_CATEGORY, DRYING_RACK);
        registry.addCategory(WASHING_CATEGORY);
        registry.addWorkstation(WASHING_CATEGORY, WASHER);

        RecipeManager recipeManager = registry.getRecipeManager();

        recipeManager.getAllRecipesFor(ModRecipes.COOLING.get()).forEach(recipe -> registry.addRecipe(new EmiCoolingRecipe(recipe.id(), recipe.value())));
        recipeManager.getAllRecipesFor(ModRecipes.MELTING.get()).forEach(recipe -> registry.addRecipe(new EmiMeltingRecipe(recipe.id(), recipe.value())));
        recipeManager.getAllRecipesFor(ModRecipes.DRYING.get()).forEach(recipe -> registry.addRecipe(new EmiDryingRecipe(recipe.id(), recipe.value())));
        recipeManager.getAllRecipesFor(ModRecipes.WASHING.get()).forEach(recipe -> registry.addRecipe(new EmiWashingRecipe(recipe.id(), recipe.value())));
    }
}
