package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.Bushcraft.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;

public class BushcraftItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public BushcraftItemTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(ConventionalItemTags.STRINGS)
            .add(ModItems.FIBER_TWINE.get());
    }
}
