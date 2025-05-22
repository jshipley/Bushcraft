package com.jship.bushcraft.client.screen;

import java.util.Set;

import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;

import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class WasherRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    private static final Component TOGGLE_WASHABLE_RECIPES_TEXT = Component.translatable("gui.recipebook.toggleRecipes.washable");
    public WasherRecipeBookComponent() {
    }

    @Override
    protected Component getRecipeFilterName() {
      return TOGGLE_WASHABLE_RECIPES_TEXT;
   }

    @Override
    protected Set<Item> getFuelItems() {
        return WasherGeoBlockEntity.getFuel().keySet();
    }
}
