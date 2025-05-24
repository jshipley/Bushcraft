package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModItems;
import com.jship.bushcraft.init.ModTags.ModItemTags;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;

import dev.architectury.fluid.FluidStack;
import lombok.val;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;

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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HAND_PUMP.get())
            .pattern("RB ")
            .pattern("WBC")
            .pattern("SSS")
            .define('B', Items.BAMBOO)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('W', ModItems.WOODEN_COG.get())
            .define('C', ConventionalItemTags.COPPER_INGOTS)
            .define('S', Items.STONE_SLAB)
            .unlockedBy(getHasName(Items.BAMBOO), has(Items.BAMBOO))
            .save(output, Bushcraft.id("crafting/hand_pump"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TREE_TAP.get())
            .pattern("SCS")
            .pattern("cSc")
            .define('S', ItemTags.WOODEN_SLABS)
            .define('C', ConventionalItemTags.COPPER_INGOTS)
            .define('c', ModItemTags.C_COPPER_NUGGETS)
            .unlockedBy(getHasName(ModItems.COPPER_NUGGET.get()), has(ModItemTags.C_COPPER_NUGGETS))
            .save(output, Bushcraft.id("crafting/tree_tap"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.WASHER.get())
            .pattern(" R ")
            .pattern("LCL")
            .pattern("LFL")
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('C', ModItems.WOODEN_COG.get())
            .define('F', ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
            .define('L', ItemTags.LOGS)
            .unlockedBy(getHasName(ModItems.WOODEN_COG.get()), has(ModItems.WOODEN_COG.get()))
            .save(output, Bushcraft.id("crafting/washer"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CRUCIBLE.get())
            .pattern("SCS")
            .pattern("S S")
            .pattern("SSS")
            .define('S', Items.STONE_SLAB)
            .define('C', ConventionalItemTags.COPPER_INGOTS)
            .unlockedBy(getHasName(Items.STONE_SLAB), has(Items.STONE_SLAB))
            .save(output, Bushcraft.id("crafting/crucible"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COPPER_BELL.get())
            .pattern("c")
            .pattern("R")
            .define('c', ModItemTags.C_COPPER_NUGGETS)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(ModItems.COPPER_NUGGET.get()), has(ModItemTags.C_COPPER_NUGGETS))
            .save(output, Bushcraft.id("crafting/copper_bell"));

        offerDrying(output, "clay_from_mud", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.MUD).result(Items.CLAY));
        offerDrying(output, "green_fiber_from_leaves", new DryingRecipe().category(CookingBookCategory.MISC).ingredient(ItemTags.LEAVES).result(ModItems.GREEN_FIBER.get()));
        offerDrying(output, "green_fiber_from_saplings", new DryingRecipe().category(CookingBookCategory.MISC).ingredient(ItemTags.SAPLINGS).result(ModItems.GREEN_FIBER.get()));
        offerDrying(output, "green_fiber_from_grass", new DryingRecipe().category(CookingBookCategory.MISC).ingredient(cTag("grass_variants")).result(ModItems.GREEN_FIBER.get()));
        offerDrying(output, "green_fiber_from_vines", new DryingRecipe().category(CookingBookCategory.MISC).ingredient(Items.VINE, Items.WEEPING_VINES, Items.TWISTING_VINES).result(ModItems.GREEN_FIBER.get()));
        offerDrying(output, "leather_from_flesh", new DryingRecipe().category(CookingBookCategory.MISC).ingredient(Items.ROTTEN_FLESH).result(Items.LEATHER));
        offerDrying(output, "dried_kelp", new DryingRecipe().category(CookingBookCategory.FOOD).ingredient(Items.KELP).result(Items.DRIED_KELP));
        // coral
        offerDrying(output, "brain_coral", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BRAIN_CORAL).result(Items.DEAD_BRAIN_CORAL));
        offerDrying(output, "brain_coral_block", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BRAIN_CORAL_BLOCK).result(Items.DEAD_BRAIN_CORAL_BLOCK));
        offerDrying(output, "brain_coral_fan", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BRAIN_CORAL_FAN).result(Items.DEAD_BRAIN_CORAL_FAN));
        offerDrying(output, "bubble_coral", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BUBBLE_CORAL).result(Items.DEAD_BUBBLE_CORAL));
        offerDrying(output, "bubble_coral_block", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BUBBLE_CORAL_BLOCK).result(Items.DEAD_BUBBLE_CORAL_BLOCK));
        offerDrying(output, "bubble_coral_fan", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.BUBBLE_CORAL_FAN).result(Items.DEAD_BUBBLE_CORAL_FAN));
        offerDrying(output, "fire_coral", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.FIRE_CORAL).result(Items.DEAD_FIRE_CORAL));
        offerDrying(output, "fire_coral_block", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.FIRE_CORAL_BLOCK).result(Items.DEAD_FIRE_CORAL_BLOCK));
        offerDrying(output, "fire_coral_fan", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.FIRE_CORAL_FAN).result(Items.DEAD_FIRE_CORAL_FAN));
        offerDrying(output, "horn_coral", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.HORN_CORAL).result(Items.DEAD_HORN_CORAL));
        offerDrying(output, "horn_coral_block", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.HORN_CORAL_BLOCK).result(Items.DEAD_HORN_CORAL_BLOCK));
        offerDrying(output, "horn_coral_fan", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.HORN_CORAL_FAN).result(Items.DEAD_HORN_CORAL_FAN));
        offerDrying(output, "tube_coral", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.TUBE_CORAL).result(Items.DEAD_TUBE_CORAL));
        offerDrying(output, "tube_coral_block", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.TUBE_CORAL_BLOCK).result(Items.DEAD_TUBE_CORAL_BLOCK));
        offerDrying(output, "tube_coral_fan", new DryingRecipe().category(CookingBookCategory.BLOCKS).ingredient(Items.TUBE_CORAL_FAN).result(Items.DEAD_TUBE_CORAL_FAN));

        offerCooling(output, "obsidian_from_lava", CoolingRecipe.builder().input(Fluids.LAVA, FluidStack.bucketAmount()).result(Items.OBSIDIAN).build());
        offerCooling(output, "ice_from_water", CoolingRecipe.builder().input(Fluids.WATER, FluidStack.bucketAmount()).result(Items.ICE).time(1800).build());

        offerMelting(output, "lava_from_gravels", MeltingRecipe.builder().ingredient(ConventionalItemTags.GRAVELS).result(Fluids.LAVA, FluidStack.bucketAmount() / 4).time(2400).build());
        offerMelting(output, "lava_from_stones", MeltingRecipe.builder().ingredient(ConventionalItemTags.STONES).result(Fluids.LAVA, FluidStack.bucketAmount() / 4).build());
        offerMelting(output, "lava_from_cobblestones", MeltingRecipe.builder().ingredient(ConventionalItemTags.COBBLESTONES).result(Fluids.LAVA, FluidStack.bucketAmount() / 4).build());
        offerMelting(output, "lava_from_obsidians", MeltingRecipe.builder().ingredient(ConventionalItemTags.OBSIDIANS).result(Fluids.LAVA, FluidStack.bucketAmount()).build());
        offerMelting(output, "water_from_ice", MeltingRecipe.builder().ingredient(Items.ICE, Items.PACKED_ICE, Items.BLUE_ICE).result(Fluids.WATER, FluidStack.bucketAmount()).time(1200).build());
        offerMelting(output, "water_from_snow_block", MeltingRecipe.builder().ingredient(Items.SNOW_BLOCK).result(Fluids.WATER, FluidStack.bucketAmount()).time(800).build());
        offerMelting(output, "water_from_snow_ball", MeltingRecipe.builder().ingredient(Items.SNOWBALL).result(Fluids.WATER, FluidStack.bucketAmount() / 4).time(400).build());

        offerWashing(output, "mud_from_dirt", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(ItemTags.DIRT), new ItemStack(Items.MUD), 0.2f, 200));
        offerWashing(output, "flint_from_gravel", new WashingRecipe("", CookingBookCategory.MISC, Ingredient.of(ConventionalItemTags.GRAVELS), new ItemStack(Items.FLINT), 0.1f, 200));
        offerWashing(output, "clay_from_terracota", new WashingRecipe("", CookingBookCategory.MISC, Ingredient.of(ItemTags.TERRACOTTA), new ItemStack(Items.CLAY), 0.2f, 200));
        offerWashing(output, "gravel_from_cobblestone", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(ConventionalItemTags.COBBLESTONES), new ItemStack(Items.GRAVEL), 0.1f, 200));
        offerWashing(output, "sand_from_sandstone", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(ConventionalItemTags.SANDSTONE_BLOCKS), new ItemStack(Items.SAND), 0.1f, 200));
        offerWashing(output, "red_sand_from_red_sandstone", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(ConventionalItemTags.RED_SANDSTONE_BLOCKS), new ItemStack(Items.RED_SAND), 0.1f, 200));
        //copper
        offerWashing(output, "oxidizing_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.COPPER_BLOCK), new ItemStack(Items.OXIDIZED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_COPPER), new ItemStack(Items.OXIDIZED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_COPPER), new ItemStack(Items.OXIDIZED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_chiseled_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.CHISELED_COPPER), new ItemStack(Items.OXIDIZED_CHISELED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_chiseled_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_CHISELED_COPPER), new ItemStack(Items.OXIDIZED_CHISELED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_chiseled_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_CHISELED_COPPER), new ItemStack(Items.OXIDIZED_CHISELED_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_cut_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.CUT_COPPER), new ItemStack(Items.OXIDIZED_CUT_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_cut_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_CUT_COPPER), new ItemStack(Items.OXIDIZED_CUT_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_cut_copper", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_CUT_COPPER), new ItemStack(Items.OXIDIZED_CUT_COPPER), 0.3f, 200));
        offerWashing(output, "oxidizing_copper_grate", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.COPPER_GRATE), new ItemStack(Items.OXIDIZED_COPPER_GRATE), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_copper_grate", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_COPPER_GRATE), new ItemStack(Items.OXIDIZED_COPPER_GRATE), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_copper_grate", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_COPPER_GRATE), new ItemStack(Items.OXIDIZED_COPPER_GRATE), 0.3f, 200));
        offerWashing(output, "oxidizing_cut_copper_stairs", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.CUT_COPPER_STAIRS), new ItemStack(Items.OXIDIZED_CUT_COPPER_STAIRS), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_cut_copper_stairs", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_CUT_COPPER_STAIRS), new ItemStack(Items.OXIDIZED_CUT_COPPER_STAIRS), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_cut_copper_stairs", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_CUT_COPPER_STAIRS), new ItemStack(Items.OXIDIZED_CUT_COPPER_STAIRS), 0.3f, 200));
        offerWashing(output, "oxidizing_cut_copper_slab", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.CUT_COPPER_SLAB), new ItemStack(Items.OXIDIZED_CUT_COPPER_SLAB), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_cut_copper_slab", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_CUT_COPPER_SLAB), new ItemStack(Items.OXIDIZED_CUT_COPPER_SLAB), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_cut_copper_slab", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_CUT_COPPER_SLAB), new ItemStack(Items.OXIDIZED_CUT_COPPER_SLAB), 0.3f, 200));
        offerWashing(output, "oxidizing_copper_door", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.COPPER_DOOR), new ItemStack(Items.OXIDIZED_COPPER_DOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_copper_door", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_COPPER_DOOR), new ItemStack(Items.OXIDIZED_COPPER_DOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_copper_door", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_COPPER_DOOR), new ItemStack(Items.OXIDIZED_COPPER_DOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_copper_trapdoor", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.COPPER_TRAPDOOR), new ItemStack(Items.OXIDIZED_COPPER_TRAPDOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_copper_trapdoor", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_COPPER_TRAPDOOR), new ItemStack(Items.OXIDIZED_COPPER_TRAPDOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_copper_trapdoor", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_COPPER_TRAPDOOR), new ItemStack(Items.OXIDIZED_COPPER_TRAPDOOR), 0.3f, 200));
        offerWashing(output, "oxidizing_copper_bulb", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.COPPER_BULB), new ItemStack(Items.OXIDIZED_COPPER_BULB), 0.3f, 200));
        offerWashing(output, "oxidizing_exposed_copper_bulb", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.EXPOSED_COPPER_BULB), new ItemStack(Items.OXIDIZED_COPPER_BULB), 0.3f, 200));
        offerWashing(output, "oxidizing_weathered_copper_bulb", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WEATHERED_COPPER_BULB), new ItemStack(Items.OXIDIZED_COPPER_BULB), 0.3f, 200));
        // concrete
        offerWashing(output, "black_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE), 0.2f, 200));
        offerWashing(output, "blue_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE), 0.2f, 200));
        offerWashing(output, "brown_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE), 0.2f, 200));
        offerWashing(output, "cyan_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE), 0.2f, 200));
        offerWashing(output, "gray_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE), 0.2f, 200));
        offerWashing(output, "green_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE), 0.2f, 200));
        offerWashing(output, "light_blue_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE), 0.2f, 200));
        offerWashing(output, "light_gray_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE), 0.2f, 200));
        offerWashing(output, "lime_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE), 0.2f, 200));
        offerWashing(output, "magenta_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE), 0.2f, 200));
        offerWashing(output, "orange_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE), 0.2f, 200));
        offerWashing(output, "pink_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE), 0.2f, 200));
        offerWashing(output, "purple_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE), 0.2f, 200));
        offerWashing(output, "red_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE), 0.2f, 200));
        offerWashing(output, "white_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE), 0.2f, 200));
        offerWashing(output, "yellow_concrete_from_powder", new WashingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE), 0.2f, 200));

        // Items
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIBER_TWINE.get())
            .requires(ModItems.GREEN_FIBER.get(), 3)
            .unlockedBy(getHasName(ModItems.GREEN_FIBER.get()), has(ModItems.GREEN_FIBER.get()))
            .save(output, Bushcraft.id("crafting/string_from_green_fiber"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOODEN_COG.get())
            .pattern(" P ")
            .pattern("PcP")
            .pattern(" P ")
            .define('P', ItemTags.PLANKS)
            .define('c', cTag("nuggets/copper"))
            .unlockedBy(getHasName(ModItems.COPPER_NUGGET.get()), has(cTag("nuggets/copper")))
            .save(output, Bushcraft.id("crafting/wooden_cog"));

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.COPPER_NUGGET.get(), RecipeCategory.MISC, Items.COPPER_INGOT, "crafting/copper_ingot", null, "crafting/copper_nugget", null);

        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ModItems.SPRUCE_SAP_BUCKET.get()), RecipeCategory.REDSTONE, ModItems.PITCH_BUCKET.get(), 1.0f, 600)
            .unlockedBy(getHasName(ModItems.SPRUCE_SAP_BUCKET.get()), has(ModItems.SPRUCE_SAP_BUCKET.get()))
            .save(output, Bushcraft.id("campfire_cooking/pitch_bucket_from_spruce_sap"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.SPRUCE_SAP_BUCKET.get()), RecipeCategory.REDSTONE, ModItems.PITCH_BUCKET.get(), 1.0f, 200)
            .unlockedBy(getHasName(ModItems.SPRUCE_SAP_BUCKET.get()), has(ModItems.SPRUCE_SAP_BUCKET.get()))
            .save(output, Bushcraft.id("smelting/pitch_bucket_from_spruce_sap"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.PITCH_BLOCK.get())
            .requires(ModItems.PITCH_BUCKET.get())
            .unlockedBy(getHasName(ModItems.PITCH_BUCKET.get()), has(ModItems.PITCH_BUCKET.get()))
            .save(output, Bushcraft.id("crafting/pitch_block_from_bucket"));
        
        twoByTwoPacker(output, RecipeCategory.REDSTONE, ModBlocks.PITCH_BLOCK.get(), ModItems.PITCH.get());
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PITCH.get(), 4)
            .requires(ModBlocks.PITCH_BLOCK.get())
            .unlockedBy(getHasName(ModBlocks.PITCH_BLOCK.get()), has(ModBlocks.PITCH_BLOCK.get()))
            .save(output, Bushcraft.id("crafting/pitch_from_pitch_block"));
        
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ModItems.BIRCH_SAP_BUCKET.get()), RecipeCategory.FOOD, ModItems.SYRUP_BUCKET.get(), 1.0f, 600)
            .unlockedBy(getHasName(ModItems.BIRCH_SAP_BUCKET.get()), has(ModItems.BIRCH_SAP_BUCKET.get()))
            .save(output, Bushcraft.id("campfire_cooking/syrup_bucket_from_birch_sap"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.BIRCH_SAP_BUCKET.get()), RecipeCategory.FOOD, ModItems.SYRUP_BUCKET.get(), 1.0f, 200)
            .unlockedBy(getHasName(ModItems.BIRCH_SAP_BUCKET.get()), has(ModItems.BIRCH_SAP_BUCKET.get()))
            .save(output, Bushcraft.id("smelting/syrup_bucket_from_birch_sap"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SYRUP_BOTTLE.get(), 4)
            .requires(ModItems.SYRUP_BUCKET.get())
            .requires(Items.GLASS_BOTTLE, 4)
            .unlockedBy(getHasName(ModItems.SYRUP_BUCKET.get()), has(ModItems.SYRUP_BUCKET.get()))
            .save(output, Bushcraft.id("crafting/syrup_bottles_from_bucket"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SYRUP_BUCKET.get())
            .requires(ModItems.SYRUP_BOTTLE.get(), 4)
            .requires(Items.BUCKET)
            .unlockedBy(getHasName(ModItems.SYRUP_BOTTLE.get()), has(ModItems.SYRUP_BOTTLE.get()))
            .save(output, Bushcraft.id("crafting/syrup_bucket_from_bottles"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.SUGAR, 3)
            .requires(ModItems.SYRUP_BOTTLE.get())
            .unlockedBy(getHasName(ModItems.SYRUP_BOTTLE.get()), has(ModItems.SYRUP_BOTTLE.get()))
            .save(output, Bushcraft.id("crafting/sugar_from_syrup_bottle"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.SUGAR, 12)
            .requires(ModItems.SYRUP_BUCKET.get())
            .unlockedBy(getHasName(ModItems.SYRUP_BUCKET.get()), has(ModItems.SYRUP_BUCKET.get()))
            .save(output, Bushcraft.id("crafting/sugar_from_syrup_bucket"));
        
        nineBlockStorageRecipes(output, RecipeCategory.MISC, Items.FLINT, RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLINT_BLOCK.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_AXE.get())
            .pattern("FF")
            .pattern("FR")
            .pattern(" R")
            .define('F', Items.FLINT)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .save(output, Bushcraft.id("crafting/flint_axe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_HOE.get())
            .pattern("FF")
            .pattern(" R")
            .pattern(" R")
            .define('F', Items.FLINT)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .save(output, Bushcraft.id("crafting/flint_hoe"));
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_PICKAXE.get())
            .pattern("FFF")
            .pattern(" R ")
            .pattern(" R ")
            .define('F', Items.FLINT)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .save(output, Bushcraft.id("crafting/flint_pickaxe"));
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_SHOVEL.get())
            .pattern("F")
            .pattern("R")
            .pattern("R")
            .define('F', Items.FLINT)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .save(output, Bushcraft.id("crafting/flint_shovel"));
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.FLINT_SWORD.get())
            .pattern("F")
            .pattern("F")
            .pattern("R")
            .define('F', Items.FLINT)
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
            .save(output, Bushcraft.id("crafting/flint_sword"));

        // Alternate vanilla recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHITE_WOOL)
            .pattern("SS")
            .pattern("SS")
            .define('S', ConventionalItemTags.STRINGS)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/wool_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.BOW)
            .pattern(" RS")
            .pattern("R S")
            .pattern(" RS")
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('S', ConventionalItemTags.STRINGS)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/bow_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.FISHING_ROD)
            .pattern("  R")
            .pattern(" RS")
            .pattern("R S")
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('S', ConventionalItemTags.STRINGS)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/fishing_rod_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.CANDLE)
            .pattern("S")
            .pattern("W")
            .define('S', ConventionalItemTags.STRINGS)
            .define('W', Items.HONEYCOMB)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/candle_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.LOOM)
            .pattern("SS")
            .pattern("PP")
            .define('S', ConventionalItemTags.STRINGS)
            .define('P', ItemTags.PLANKS)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/loom_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.SCAFFOLDING, 6)
            .pattern("BSB")
            .pattern("B B")
            .pattern("B B")
            .define('S', ConventionalItemTags.STRINGS)
            .define('B', Items.BAMBOO)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/scaffolding_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.LEAD, 2)
            .pattern("SS ")
            .pattern("SB ")
            .pattern("  S")
            .define('S', ConventionalItemTags.STRINGS)
            .define('B', ConventionalItemTags.SLIME_BALLS)
            .unlockedBy("has_slime", has(ConventionalItemTags.SLIME_BALLS))
            .save(output, Bushcraft.id("crafting/lead_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CROSSBOW)
            .pattern("RIR")
            .pattern("STS")
            .pattern(" R ")
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('I', ConventionalItemTags.IRON_INGOTS)
            .define('S', ConventionalItemTags.STRINGS)
            .define('T', Items.TRIPWIRE_HOOK)
            .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
            .save(output, Bushcraft.id("crafting/crossbow_from_c_strings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH)
            .pattern("P")
            .pattern("R")
            .define('P', ModItems.PITCH.get())
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .unlockedBy(getHasName(ModItems.PITCH.get()), has(ModItems.PITCH.get()))
            .save(output, Bushcraft.id("crafting/torch_from_pitch"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SOUL_TORCH)
            .pattern("P")
            .pattern("R")
            .pattern("S")
            .define('P', ModItems.PITCH.get())
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
            .unlockedBy(getHasName(ModItems.PITCH.get()), has(ModItems.PITCH.get()))
            .save(output, Bushcraft.id("crafting/soul_torch_from_pitch"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CAMPFIRE)
            .pattern(" R ")
            .pattern("RPR")
            .pattern("LLL")
            .define('P', ModItems.PITCH.get())
            .define('R', ConventionalItemTags.WOODEN_RODS)
            .define('L', ItemTags.LOGS)
            .unlockedBy(getHasName(ModItems.PITCH.get()), has(ModItems.PITCH.get()))
            .save(output, Bushcraft.id("crafting/campfire_from_pitch"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH)
            .pattern("GBP")
            .define('G', ConventionalItemTags.GUNPOWDERS)
            .define('B', Items.BLAZE_POWDER)
            .define('P', ModItems.PITCH.get())
            .unlockedBy(getHasName(ModItems.PITCH.get()), has(ModItems.PITCH.get()))
            .save(output, Bushcraft.id("crafting/fire_charge_from_pitch"));
    }

    public void offerCooling(RecipeOutput output, String name, CoolingRecipe recipe) {
        val id = Bushcraft.id(name).withPrefix("cooling/");
        val criteria = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(getHasName(ModBlocks.CRUCIBLE.get()), has(ModBlocks.CRUCIBLE.get()));
        output.accept(id, recipe, criteria.build(id));
    }

    public void offerDrying(RecipeOutput output, String name, DryingRecipe recipe) {
        val id = Bushcraft.id(name).withPrefix("drying/");
        val criteria = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(getHasName(ModBlocks.DRYING_RACK.get()), has(ModBlocks.DRYING_RACK.get()));
        output.accept(id, recipe, criteria.build(id));
    }

    public void offerMelting(RecipeOutput output, String name, MeltingRecipe recipe) {
        val id = Bushcraft.id(name).withPrefix("melting/");
        val criteria = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(getHasName(ModBlocks.CRUCIBLE.get()), has(ModBlocks.CRUCIBLE.get()));
        output.accept(id, recipe, criteria.build(id));
    }

    public void offerWashing(RecipeOutput output, String name, WashingRecipe recipe) {
        val id = Bushcraft.id(name).withPrefix("drying/");
        val criteria = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(getHasName(ModBlocks.WASHER.get()), has(ModBlocks.WASHER.get()));
        output.accept(id, recipe, criteria.build(id));
    }

    protected TagKey<Item> cTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
