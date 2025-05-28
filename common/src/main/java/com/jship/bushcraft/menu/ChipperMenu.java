package com.jship.bushcraft.menu;

import com.jship.bushcraft.init.ModMenus;
import com.jship.bushcraft.init.ModRecipes;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;

public class ChipperMenu extends AbstractFurnaceMenu {
    public ChipperMenu(int i, Inventory inventory) {
        super(ModMenus.CHIPPER.get(), ModRecipes.CHIPPING.get(), RecipeBookType.FURNACE, i, inventory);
    }

    public ChipperMenu(int i, Inventory inventory, Container container, ContainerData containerData) {
        super(ModMenus.CHIPPER.get(), ModRecipes.CHIPPING.get(), RecipeBookType.FURNACE, i, inventory, container, containerData);
    }
}
