package com.jship.bushcraft.fabric.datagen;

import java.util.concurrent.CompletableFuture;

import com.jship.bushcraft.init.ModItems;
import com.jship.bushcraft.init.ModTags.ModItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
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
        getOrCreateTagBuilder(ModItemTags.C_COPPER_NUGGETS)
            .add(ModItems.COPPER_NUGGET.get());
        getOrCreateTagBuilder(cTag("grass_variants"))
            .add(Items.FERN).add(Items.LARGE_FERN).add(Items.MOSS_CARPET).add(Items.SEAGRASS).add(Items.SHORT_GRASS).add(Items.TALL_GRASS).add(Items.NETHER_SPROUTS).add(Items.WARPED_ROOTS).add(Items.CRIMSON_ROOTS);
        getOrCreateTagBuilder(ConventionalItemTags.FOODS)
            .add(ModItems.SYRUP_BOTTLE.get());
        getOrCreateTagBuilder(ConventionalItemTags.DRINK_CONTAINING_BOTTLE)
            .add(ModItems.SYRUP_BOTTLE.get());
        getOrCreateTagBuilder(cTag("drinks/syrup"))
            .add(ModItems.SYRUP_BOTTLE.get());
        getOrCreateTagBuilder(ConventionalItemTags.DRINKS)
            .addTag(cTag("drinks/syrup"));
        getOrCreateTagBuilder(ItemTags.AXES)
            .add(ModItems.FLINT_AXE.get());
        getOrCreateTagBuilder(ItemTags.HOES)
            .add(ModItems.FLINT_HOE.get());
        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(ModItems.FLINT_PICKAXE.get());
        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(ModItems.FLINT_SHOVEL.get());
        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(ModItems.FLINT_SWORD.get());
        
    }

    protected TagKey<Item> cTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
    }
}
