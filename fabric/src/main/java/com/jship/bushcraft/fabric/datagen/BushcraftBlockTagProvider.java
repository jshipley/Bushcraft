package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.Bushcraft.ModBlocks;
import com.jship.bushcraft.Bushcraft.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BushcraftBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BushcraftBlockTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(BlockTags.INFINIBURN_OVERWORLD)
            .add(ModFluids.SPRUCE_SAP_SOURCE_BLOCK.get())
            .add(ModBlocks.PITCH_BLOCK.get());
        getOrCreateTagBuilder(ModBlocks.PRODUCES_SAP)
            .forceAddTag(BlockTags.SPRUCE_LOGS)
            .forceAddTag(BlockTags.BIRCH_LOGS);
    }

    protected TagKey<Item> cTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
