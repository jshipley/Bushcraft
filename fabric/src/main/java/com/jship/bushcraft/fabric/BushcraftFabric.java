package com.jship.bushcraft.fabric;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
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

        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> InventoryStorage.of((WorldlyContainer) blockEntity, null), ModBlockEntities.DRYING_RACK.get());
        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((SpiritFluidStorageImpl)((WasherGeoBlockEntity)blockEntity).fluidStorage).fabricFluidStorage, ModBlockEntities.WASHER.get());
        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((SpiritFluidStorageImpl)((TreeTapBlockEntity)blockEntity).fluidStorage).fabricFluidStorage, ModBlockEntities.TREE_TAP.get());
    }
}
