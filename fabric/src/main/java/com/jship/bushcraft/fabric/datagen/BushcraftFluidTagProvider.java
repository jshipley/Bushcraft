package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.init.ModFluids;
import com.jship.bushcraft.init.ModTags.ModFluidTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class BushcraftFluidTagProvider extends FabricTagProvider.FluidTagProvider {

    public BushcraftFluidTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(ModFluidTags.C_BIRCH_SAP)
            .add(ModFluids.BIRCH_SAP.get(), ModFluids.BIRCH_SAP_FLOWING.get());
        getOrCreateTagBuilder(ModFluidTags.C_SPRUCE_SAP)
            .add(ModFluids.SPRUCE_SAP.get(), ModFluids.SPRUCE_SAP_FLOWING.get());
        getOrCreateTagBuilder(ModFluidTags.C_SAPS)
            .addTag(ModFluidTags.C_BIRCH_SAP)
            .addTag(ModFluidTags.C_SPRUCE_SAP);
        // for mod compatibility
        getOrCreateTagBuilder(cTag("fuel"))
            .add(ModFluids.SPRUCE_SAP.get());
    }

    protected TagKey<Fluid> cTag(String name) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
