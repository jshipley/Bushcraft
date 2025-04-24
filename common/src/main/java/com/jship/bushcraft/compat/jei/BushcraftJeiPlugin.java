package com.jship.bushcraft.compat.jei;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.compat.jei.category.BushcraftJeiDryingCategory;
import com.jship.bushcraft.recipe.DryingRecipe;
import java.util.List;
import java.util.stream.Collectors;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class BushcraftJeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = Bushcraft.id("jei_plugin");
    public static final RecipeType<DryingRecipe> DRYING_RECIPE = RecipeType.create(Bushcraft.MOD_ID, "drying", DryingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new BushcraftJeiDryingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<RecipeHolder<DryingRecipe>> recipeHolders = recipeManager.getAllRecipesFor(Bushcraft.ModRecipes.DRYING.get());
        registration.addRecipes(DRYING_RECIPE, recipeHolders.stream().map(r -> r.value()).collect(Collectors.toList()));
        Bushcraft.LOGGER.info("[Bushcraft] Registered drying recipes: {}", recipeHolders.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Bushcraft.ModBlocks.DRYING_RACK.get()), DRYING_RECIPE);
    }
}
