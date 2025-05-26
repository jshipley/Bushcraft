package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModTags.ModBlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BushcraftBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BushcraftBlockTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(ModBlocks.DRYING_RACK.get(), ModBlocks.HAND_PUMP.get(), ModBlocks.TREE_TAP.get());
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.CRUCIBLE.get(), ModBlocks.FLINT_BLOCK.get(), ModBlocks.WASHER.get());
        getOrCreateTagBuilder(BlockTags.INFINIBURN_OVERWORLD)
            .add(ModBlocks.SPRUCE_SAP_SOURCE.get())
            .add(ModBlocks.PITCH_BLOCK.get());
        getOrCreateTagBuilder(ModBlockTags.PRODUCES_SAP)
            .forceAddTag(BlockTags.SPRUCE_LOGS)
            .forceAddTag(BlockTags.BIRCH_LOGS);
        getOrCreateTagBuilder(ModBlockTags.C_STICKY)
            .add(ModBlocks.PITCH_BLOCK.get())
            .add(Blocks.HONEY_BLOCK, Blocks.SLIME_BLOCK);
        getOrCreateTagBuilder(cTag("storage_blocks/flint"))
            .add(ModBlocks.FLINT_BLOCK.get());
        getOrCreateTagBuilder(cTag("storage_blocks"))
            .addTag(cTag("storage_blocks/flint"));
        getOrCreateTagBuilder(ModBlockTags.NEEDS_FLINT_TOOL)
            .forceAddTag(BlockTags.NEEDS_IRON_TOOL);
        getOrCreateTagBuilder(ModBlockTags.INCORRECT_FOR_FLINT_TOOL)
            .forceAddTag(BlockTags.INCORRECT_FOR_IRON_TOOL);
        getOrCreateTagBuilder(ModBlockTags.LOW_HEAT_SOURCES)
            .add(Blocks.TORCH, Blocks.SOUL_TORCH)
            .forceAddTag(BlockTags.CANDLES)
            .forceAddTag(BlockTags.CANDLE_CAKES);
        getOrCreateTagBuilder(ModBlockTags.MEDIUM_HEAT_SOURCES)
            .add(Blocks.MAGMA_BLOCK, Blocks.FIRE)
            .forceAddTag(BlockTags.CAMPFIRES)
            .addOptional(ResourceLocation.fromNamespaceAndPath("blazingbamboo", "blazing_bamboo_bundle"))
            .addOptional(ResourceLocation.fromNamespaceAndPath("blazingbamboo", "blazing_stone"))
            .addOptional(ResourceLocation.fromNamespaceAndPath("farmersdelight", "stove"));
        getOrCreateTagBuilder(ModBlockTags.HIGH_HEAT_SOURCES)
            .add(Blocks.LAVA);
        getOrCreateTagBuilder(ModBlockTags.HEAT_SOURCES)
            .addTag(ModBlockTags.LOW_HEAT_SOURCES)
            .addTag(ModBlockTags.MEDIUM_HEAT_SOURCES)
            .addTag(ModBlockTags.HIGH_HEAT_SOURCES);
        getOrCreateTagBuilder(ModBlockTags.COLD_SOURCES)
            .forceAddTag(BlockTags.ICE)
            .forceAddTag(BlockTags.SNOW);
    }

    protected TagKey<Block> cTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
