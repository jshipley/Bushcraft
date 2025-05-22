package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.Bushcraft.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BushcraftItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public BushcraftItemTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        getOrCreateTagBuilder(ConventionalItemTags.STRINGS)
            .add(ModItems.FIBER_TWINE.get());
        getOrCreateTagBuilder(cTag("nuggets/copper"))
            .add(ModItems.COPPER_NUGGET.get());
        getOrCreateTagBuilder(cTag("grass_variants"))
            .add(Items.FERN).add(Items.LARGE_FERN).add(Items.MOSS_CARPET).add(Items.SEAGRASS).add(Items.SHORT_GRASS).add(Items.TALL_GRASS).add(Items.NETHER_SPROUTS).add(Items.WARPED_ROOTS).add(Items.CRIMSON_ROOTS);
    }

    protected TagKey<Item> cTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
