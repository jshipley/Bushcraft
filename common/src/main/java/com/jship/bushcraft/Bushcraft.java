package com.jship.bushcraft;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.common.base.Suppliers;
import com.jship.bushcraft.client.screen.ChipperScreen;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModFluids;
import com.jship.bushcraft.init.ModItems;
import com.jship.bushcraft.init.ModMenus;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.init.ModSounds;
import com.mojang.logging.LogUtils;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.resources.ResourceLocation;

public final class Bushcraft {

    public static final String MOD_ID = "bushcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        ModFluids.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModItems.init();
        ModRecipes.init();
        ModSounds.init();
    }

    public static void clientInit() {
        MenuRegistry.registerScreenFactory(ModMenus.CHIPPER.get(), ChipperScreen::new);
    }
}
