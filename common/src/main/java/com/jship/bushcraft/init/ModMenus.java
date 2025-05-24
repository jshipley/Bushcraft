package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.menu.WasherMenu;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
        public static final Registrar<MenuType<?>> MENUS = Bushcraft.MANAGER.get().get(Registries.MENU);

        public static RegistrySupplier<MenuType<WasherMenu>> WASHER = MENUS.register(
                Bushcraft.id("washer"),
                () -> new MenuType<WasherMenu>(
                        WasherMenu::new,
                        FeatureFlags.VANILLA_SET));

        public static void init() {
        }
    }
