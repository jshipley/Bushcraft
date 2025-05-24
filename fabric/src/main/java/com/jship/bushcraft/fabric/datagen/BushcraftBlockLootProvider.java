package com.jship.bushcraft.fabric.datagen;


import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.init.ModBlocks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

public class BushcraftBlockLootProvider extends FabricBlockLootTableProvider {

    protected BushcraftBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.COPPER_BELL.get());
        dropSelf(ModBlocks.CRUCIBLE.get());
        dropSelf(ModBlocks.DRYING_RACK.get());
        dropSelf(ModBlocks.FLINT_BLOCK.get());
        dropSelf(ModBlocks.HAND_PUMP.get());
        dropSelf(ModBlocks.PITCH_BLOCK.get());
        dropSelf(ModBlocks.TREE_TAP.get());
        dropSelf(ModBlocks.WASHER.get());
    }
}
