package com.jship.bushcraft.fabric.datagen;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.Bushcraft.ModBlocks;
import com.jship.bushcraft.Bushcraft.ModItems;
import com.jship.bushcraft.recipe.DryingRecipe;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;

public class BushcraftRecipeProvider extends FabricRecipeProvider {

    public BushcraftRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Mod blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRYING_RACK.get())
            .pattern("SRS")
            .pattern("L L")
            .define('S', ItemTags.WOODEN_SLABS)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('L', ItemTags.LOGS_THAT_BURN)
            .unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN))
            .save(output, Bushcraft.id("crafting/drying_rack"));

        // TODO add achievements to unlock recipes
        output.accept(Bushcraft.id("drying/clay"), new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.MUD).result(Items.CLAY), null);
        output.accept(Bushcraft.id("drying/green_fiber_from_leaves"), new DryingRecipe().category(CookingBookCategory.MISC).ingredient(ItemTags.LEAVES).result(ModItems.GREEN_FIBER.get()), null);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIBER_TWINE.get())
            .requires(ModItems.GREEN_FIBER.get(), 3)
            .unlockedBy(getHasName(ModItems.GREEN_FIBER.get()), has(ModItems.GREEN_FIBER.get()))
            .save(output, Bushcraft.id("crafting/string_from_green_fiber"));
        
        // Alternate vanilla recipes, TODO: fix up categories/groups to match
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHITE_WOOL)
            .pattern("SS")
            .pattern("SS")
            .define('S', ConventionalItemTags.STRINGS)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/wool_from_c_strings"));        
    }
}
