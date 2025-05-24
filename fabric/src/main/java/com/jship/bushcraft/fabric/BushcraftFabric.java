package com.jship.bushcraft.fabric;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.spiritapi.api.fluid.SpiritFluidStorageProvider;
import com.jship.spiritapi.api.fluid.fabric.SpiritFluidStorageImpl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.WorldlyContainer;

public final class BushcraftFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Bushcraft.init();

        ItemStorage.SIDED.registerForBlockEntities(
                (blockEntity, direction) -> InventoryStorage.of((WorldlyContainer) blockEntity, null),
                ModBlockEntities.CRUCIBLE.get(), ModBlockEntities.DRYING_RACK.get(), ModBlockEntities.WASHER.get());
        FluidStorage.SIDED.registerForBlockEntities(
                (blockEntity,
                        direction) -> ((SpiritFluidStorageImpl) ((SpiritFluidStorageProvider) blockEntity)
                                .getFluidStorage(direction)).fabricFluidStorage,
                ModBlockEntities.CRUCIBLE.get(), ModBlockEntities.TREE_TAP.get(), ModBlockEntities.WASHER.get());
    }
}
