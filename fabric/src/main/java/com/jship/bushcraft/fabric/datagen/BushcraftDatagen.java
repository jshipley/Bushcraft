package com.jship.bushcraft.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BushcraftDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(BushcraftModelProvider::new);
        pack.addProvider(BushcraftBlockLootProvider::new);
        pack.addProvider(BushcraftRecipeProvider::new);
        pack.addProvider(BushcraftBlockTagProvider::new);
        pack.addProvider(BushcraftFluidTagProvider::new);
        pack.addProvider(BushcraftItemTagProvider::new);
    }
}
