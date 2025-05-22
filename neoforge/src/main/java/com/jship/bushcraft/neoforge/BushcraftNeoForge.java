package com.jship.bushcraft.neoforge;

import com.jship.bushcraft.Bushcraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(Bushcraft.MOD_ID)
@EventBusSubscriber(modid = Bushcraft.MOD_ID)
public final class BushcraftNeoForge {

    public BushcraftNeoForge() {
        // Run our common setup.
        Bushcraft.init();


    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        // item handlers
        // fluid handlers
    }
}
