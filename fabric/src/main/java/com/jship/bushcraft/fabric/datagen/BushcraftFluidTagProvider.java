package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.Bushcraft.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class BushcraftFluidTagProvider extends FabricTagProvider.FluidTagProvider {

    public BushcraftFluidTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(ModFluids.C_BIRCH_SAP)
            .add(ModFluids.BIRCH_SAP.get(), ModFluids.BIRCH_SAP_FLOWING.get());
        getOrCreateTagBuilder(ModFluids.C_SPRUCE_SAP)
            .add(ModFluids.SPRUCE_SAP.get(), ModFluids.SPRUCE_SAP_FLOWING.get());
        getOrCreateTagBuilder(ModFluids.C_SAPS)
            .addTag(ModFluids.C_BIRCH_SAP)
            .addTag(ModFluids.C_SPRUCE_SAP);
    }

    protected TagKey<Fluid> cTag(String name) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
