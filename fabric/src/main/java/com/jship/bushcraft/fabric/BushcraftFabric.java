package com.jship.bushcraft.fabric;

import com.jship.bushcraft.Bushcraft;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.WorldlyContainer;

public final class BushcraftFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Bushcraft.init();

        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> InventoryStorage.of((WorldlyContainer) blockEntity, null), Bushcraft.ModBlockEntities.DRYING_RACK.get());
    }
}
