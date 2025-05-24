package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    public class ModBlockTags {
        public static final TagKey<Block> PRODUCES_SAP = TagKey.create(Registries.BLOCK, Bushcraft.id("produces_sap"));
        public static final TagKey<Block> C_STICKY = TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("c", "sticky"));
        public static final TagKey<Block> NEEDS_FLINT_TOOL = TagKey.create(Registries.BLOCK,
                Bushcraft.id("needs_flint_tool"));
        public static final TagKey<Block> INCORRECT_FOR_FLINT_TOOL = TagKey.create(Registries.BLOCK,
                Bushcraft.id("incorrect_for_flint_tool"));
        // The HEAT_SOURCE tags will be going away and replaced by recipes, but it's
        // good enough for now
        public static final TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK,
                Bushcraft.id("heat_sources"));
        public static final TagKey<Block> LOW_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
                Bushcraft.id("heat_sources/low"));
        public static final TagKey<Block> MEDIUM_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
                Bushcraft.id("heat_sources/medium"));
        public static final TagKey<Block> HIGH_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
                Bushcraft.id("heat_sources/high"));
        public static final TagKey<Block> COLD_SOURCES = TagKey.create(Registries.BLOCK,
                Bushcraft.id("cold_sources"));
    }

    public class ModFluidTags {
        public static final TagKey<Fluid> C_SAPS = TagKey.create(
                Registries.FLUID,
                ResourceLocation.fromNamespaceAndPath("c", "saps"));
        public static final TagKey<Fluid> C_SPRUCE_SAP = TagKey.create(
                Registries.FLUID,
                ResourceLocation.fromNamespaceAndPath("c", "saps/spruce"));
        public static final TagKey<Fluid> C_BIRCH_SAP = TagKey.create(
                Registries.FLUID,
                ResourceLocation.fromNamespaceAndPath("c", "saps/birch"));
    }

    public class ModItemTags {
        public static final TagKey<Item> C_COPPER_NUGGETS = TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "nuggets/copper"));
    }

}
