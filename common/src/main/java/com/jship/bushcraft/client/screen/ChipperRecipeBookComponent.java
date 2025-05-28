package com.jship.bushcraft.client.screen;

import java.util.Set;

import com.jship.bushcraft.block.entity.ChipperGeoBlockEntity;

import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class ChipperRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    private static final Component TOGGLE_CHIPPABLE_RECIPES_TEXT = Component.translatable("gui.recipebook.toggleRecipes.chippable");
    public ChipperRecipeBookComponent() {
    }

    @Override
    protected Component getRecipeFilterName() {
      return TOGGLE_CHIPPABLE_RECIPES_TEXT;
   }

    @Override
    protected Set<Item> getFuelItems() {
        return ChipperGeoBlockEntity.getFuel().keySet();
    }
}
