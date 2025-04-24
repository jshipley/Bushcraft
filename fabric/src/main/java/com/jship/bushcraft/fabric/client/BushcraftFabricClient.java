package com.jship.bushcraft.fabric.client;

import com.jship.bushcraft.Bushcraft;
import net.fabricmc.api.ClientModInitializer;

public final class BushcraftFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Bushcraft.clientInit();
    }
}
