package com.jship.bushcraft.menu;

import com.jship.bushcraft.init.ModMenus;
import com.jship.bushcraft.init.ModRecipes;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;

public class WasherMenu extends AbstractFurnaceMenu {
    public WasherMenu(int i, Inventory inventory) {
        super(ModMenus.WASHER.get(), ModRecipes.WASHING.get(), RecipeBookType.FURNACE, i, inventory);
    }

    public WasherMenu(int i, Inventory inventory, Container container, ContainerData containerData) {
        super(ModMenus.WASHER.get(), ModRecipes.WASHING.get(), RecipeBookType.FURNACE, i, inventory, container, containerData);
    }
    
}
