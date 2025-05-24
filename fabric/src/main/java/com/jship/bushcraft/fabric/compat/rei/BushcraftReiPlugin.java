package com.jship.bushcraft.fabric.compat.rei;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.fabric.compat.rei.category.BushcraftReiCoolingCategory;
import com.jship.bushcraft.fabric.compat.rei.category.BushcraftReiDryingCategory;
import com.jship.bushcraft.fabric.compat.rei.category.BushcraftReiMeltingCategory;
import com.jship.bushcraft.fabric.compat.rei.category.BushcraftReiWashingCategory;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiCoolingDisplay;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiDryingDisplay;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiMeltingDisplay;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiWashingDisplay;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.ResourceLocation;

public class BushcraftReiPlugin implements REIClientPlugin {

    private static final ResourceLocation ID = Bushcraft.id("rei_plugin");
    public static final CategoryIdentifier<BushcraftReiCoolingDisplay> COOLING = CategoryIdentifier.of(Bushcraft.MOD_ID, "rei_cooling_category");
    public static final CategoryIdentifier<BushcraftReiDryingDisplay> DRYING = CategoryIdentifier.of(Bushcraft.MOD_ID, "rei_drying_category");
    public static final CategoryIdentifier<BushcraftReiMeltingDisplay> MELTING = CategoryIdentifier.of(Bushcraft.MOD_ID, "rei_melting_category");
    public static final CategoryIdentifier<BushcraftReiWashingDisplay> WASHING = CategoryIdentifier.of(Bushcraft.MOD_ID, "rei_washing_category");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new BushcraftReiCoolingCategory());
        registry.addWorkstations(COOLING, EntryStacks.of(ModBlocks.CRUCIBLE.get()));
        registry.add(new BushcraftReiDryingCategory());
        registry.addWorkstations(DRYING, EntryStacks.of(ModBlocks.DRYING_RACK.get()));
        registry.add(new BushcraftReiMeltingCategory());
        registry.addWorkstations(MELTING, EntryStacks.of(ModBlocks.CRUCIBLE.get()));
        registry.add(new BushcraftReiWashingCategory());
        registry.addWorkstations(WASHING, EntryStacks.of(ModBlocks.WASHER.get()));
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(COOLING, BushcraftReiCoolingDisplay.serializer(BushcraftReiCoolingDisplay::new));
        registry.register(DRYING, BushcraftReiDryingDisplay.serializer(BushcraftReiDryingDisplay::new));
        registry.register(MELTING, BushcraftReiMeltingDisplay.serializer(BushcraftReiMeltingDisplay::new));
        registry.register(WASHING, BushcraftReiWashingDisplay.serializer(BushcraftReiWashingDisplay::new));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(CoolingRecipe.class, ModRecipes.COOLING.get(), BushcraftReiCoolingDisplay::new);
        registry.registerRecipeFiller(DryingRecipe.class, ModRecipes.DRYING.get(), BushcraftReiDryingDisplay::new);
        registry.registerRecipeFiller(MeltingRecipe.class, ModRecipes.MELTING.get(), BushcraftReiMeltingDisplay::new);
        registry.registerRecipeFiller(WashingRecipe.class, ModRecipes.WASHING.get(), BushcraftReiWashingDisplay::new);
    }

    @Override
    public String getPluginProviderName() {
        return ID.toString();
    }
}
