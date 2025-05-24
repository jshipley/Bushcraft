package com.jship.bushcraft.compat.jei;

import java.util.stream.Collectors;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.compat.jei.category.BushcraftJeiCoolingCategory;
import com.jship.bushcraft.compat.jei.category.BushcraftJeiDryingCategory;
import com.jship.bushcraft.compat.jei.category.BushcraftJeiMeltingCategory;
import com.jship.bushcraft.compat.jei.category.BushcraftJeiWashingCategory;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;

import lombok.val;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class BushcraftJeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = Bushcraft.id("jei_plugin");
    public static final RecipeType<CoolingRecipe> COOLING_RECIPE = RecipeType.create(Bushcraft.MOD_ID, "cooling", CoolingRecipe.class);
    public static final RecipeType<DryingRecipe> DRYING_RECIPE = RecipeType.create(Bushcraft.MOD_ID, "drying", DryingRecipe.class);
    public static final RecipeType<MeltingRecipe> MELTING_RECIPE = RecipeType.create(Bushcraft.MOD_ID, "melting", MeltingRecipe.class);
    public static final RecipeType<WashingRecipe> WASHING_RECIPE = RecipeType.create(Bushcraft.MOD_ID, "washing", WashingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new BushcraftJeiCoolingCategory(guiHelper));
        registration.addRecipeCategories(new BushcraftJeiDryingCategory(guiHelper));
        registration.addRecipeCategories(new BushcraftJeiMeltingCategory(guiHelper));
        registration.addRecipeCategories(new BushcraftJeiWashingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        val recipeManager = Minecraft.getInstance().level.getRecipeManager();
        
        val coolingRecipeHolders = recipeManager.getAllRecipesFor(ModRecipes.COOLING.get());
        registration.addRecipes(COOLING_RECIPE, coolingRecipeHolders.stream().map(r -> r.value()).collect(Collectors.toList()));
        Bushcraft.LOGGER.info("[Bushcraft] Registered cooling recipes: {}", coolingRecipeHolders.size());

        val dryingRecipeHolders = recipeManager.getAllRecipesFor(ModRecipes.DRYING.get());
        registration.addRecipes(DRYING_RECIPE, dryingRecipeHolders.stream().map(r -> r.value()).collect(Collectors.toList()));
        Bushcraft.LOGGER.info("[Bushcraft] Registered drying recipes: {}", dryingRecipeHolders.size());

        val meltingRecipeHolders = recipeManager.getAllRecipesFor(ModRecipes.MELTING.get());
        registration.addRecipes(MELTING_RECIPE, meltingRecipeHolders.stream().map(r -> r.value()).collect(Collectors.toList()));
        Bushcraft.LOGGER.info("[Bushcraft] Registered melting recipes: {}", meltingRecipeHolders.size());

        val washingRecipeHolders = recipeManager.getAllRecipesFor(ModRecipes.WASHING.get());
        registration.addRecipes(WASHING_RECIPE, washingRecipeHolders.stream().map(r -> r.value()).collect(Collectors.toList()));
        Bushcraft.LOGGER.info("[Bushcraft] Registered washing recipes: {}", washingRecipeHolders.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DRYING_RACK.get()), DRYING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRUCIBLE.get()), COOLING_RECIPE, MELTING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WASHER.get()), WASHING_RECIPE);
    }
}
