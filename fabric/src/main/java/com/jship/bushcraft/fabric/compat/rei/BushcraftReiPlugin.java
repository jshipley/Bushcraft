package com.jship.bushcraft.fabric.compat.rei;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.Bushcraft.ModRecipes;
import com.jship.bushcraft.fabric.compat.rei.category.BushcraftReiDryingCategory;
import com.jship.bushcraft.fabric.compat.rei.display.BushcraftReiDryingDisplay;
import com.jship.bushcraft.recipe.DryingRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.ResourceLocation;

public class BushcraftReiPlugin implements REIClientPlugin {

    private static final ResourceLocation ID = Bushcraft.id("rei_plugin");
    public static final CategoryIdentifier<BushcraftReiDryingDisplay> DRYING = CategoryIdentifier.of(Bushcraft.MOD_ID, "rei_drying_category");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new BushcraftReiDryingCategory());
        registry.addWorkstations(DRYING, EntryStacks.of(Bushcraft.ModBlocks.DRYING_RACK.get()));
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(DRYING, BushcraftReiDryingDisplay.serializer(BushcraftReiDryingDisplay::new));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(DryingRecipe.class, ModRecipes.DRYING.get(), BushcraftReiDryingDisplay::new);
    }

    @Override
    public String getPluginProviderName() {
        return ID.toString();
    }
}
