package com.jship.bushcraft.fabric.datagen;

import com.jship.bushcraft.Bushcraft.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

public class BushcraftBlockLootProvider extends FabricBlockLootTableProvider {

    protected BushcraftBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.DRYING_RACK.get());
    }
}
