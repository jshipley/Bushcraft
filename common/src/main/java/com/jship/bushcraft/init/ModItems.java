package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.item.ModTiers;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

public class ModItems {
        public static final Registrar<Item> ITEMS = Bushcraft.MANAGER.get().get(Registries.ITEM);

        public static final RegistrySupplier<Item> GREEN_FIBER = ITEMS.register(Bushcraft.id("green_fiber"),
                        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> FIBER_TWINE = ITEMS.register(Bushcraft.id("fiber_twine"),
                        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> COPPER_NUGGET = ITEMS.register(Bushcraft.id("copper_nugget"),
                        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> WOODEN_COG = ITEMS.register(Bushcraft.id("wooden_cog"),
                        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> PITCH_BUCKET = ITEMS.register(Bushcraft.id("pitch_bucket"),
                        () -> new Item(
                                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                                                        .arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> PITCH = ITEMS.register(Bushcraft.id("pitch"),
                        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> SYRUP_BOTTLE = ITEMS.register(Bushcraft.id("syrup_bottle"),
                        () -> new HoneyBottleItem((new Item.Properties()).craftRemainder(Items.GLASS_BOTTLE)
                                        .food(Foods.HONEY_BOTTLE).stacksTo(16)
                                        .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)));

        public static final RegistrySupplier<Item> BIRCH_SAP_BUCKET = ITEMS.register(Bushcraft.id("birch_sap_bucket"),
                        () -> new ArchitecturyBucketItem(ModFluids.BIRCH_SAP,
                                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                                                        .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
        public static final RegistrySupplier<Item> SPRUCE_SAP_BUCKET = ITEMS.register(Bushcraft.id("spruce_sap_bucket"),
                        () -> new ArchitecturyBucketItem(ModFluids.SPRUCE_SAP,
                                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                                                        .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
        public static final RegistrySupplier<Item> SYRUP_BUCKET = ITEMS.register(Bushcraft.id("syrup_bucket"),
                        () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)));

        public static final RegistrySupplier<Item> FLINT_AXE = ITEMS.register(Bushcraft.id("flint_axe"),
                        () -> new AxeItem(ModTiers.FLINT,
                                        new Item.Properties()
                                                        .attributes(AxeItem.createAttributes(ModTiers.FLINT, 6.0f,
                                                                        -3.1f))
                                                        .arch$tab(CreativeModeTabs.COMBAT)));
        public static final RegistrySupplier<Item> FLINT_HOE = ITEMS.register(Bushcraft.id("flint_hoe"),
                        () -> new HoeItem(ModTiers.FLINT,
                                        new Item.Properties()
                                                        .attributes(HoeItem.createAttributes(ModTiers.FLINT, -2.0f,
                                                                        -1.0f))
                                                        .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));
        public static final RegistrySupplier<Item> FLINT_PICKAXE = ITEMS.register(Bushcraft.id("flint_pickaxe"),
                        () -> new PickaxeItem(ModTiers.FLINT,
                                        new Item.Properties()
                                                        .attributes(PickaxeItem.createAttributes(ModTiers.FLINT, 1.0f,
                                                                        -2.8f))
                                                        .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));
        public static final RegistrySupplier<Item> FLINT_SHOVEL = ITEMS.register(Bushcraft.id("flint_shovel"),
                        () -> new ShovelItem(ModTiers.FLINT,
                                        new Item.Properties()
                                                        .attributes(ShovelItem.createAttributes(ModTiers.FLINT, 1.5f,
                                                                        -3.0f))
                                                        .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));
        public static final RegistrySupplier<Item> FLINT_SWORD = ITEMS.register(Bushcraft.id("flint_sword"),
                        () -> new SwordItem(ModTiers.FLINT,
                                        new Item.Properties()
                                                        .attributes(SwordItem.createAttributes(ModTiers.FLINT, 3,
                                                                        -2.4f))
                                                        .arch$tab(CreativeModeTabs.COMBAT)));

        public static void init() {
                WOODEN_COG.listen(cog -> FuelRegistry.register(100, cog));
                PITCH_BUCKET.listen(pitchBucket -> FuelRegistry.register(6400, pitchBucket));
                SPRUCE_SAP_BUCKET.listen(sapBucket -> FuelRegistry.register(1600, sapBucket));
                PITCH.listen(pitch -> FuelRegistry.register(1600, pitch));
        }

        public static ResourceKey<Item> itemKey(ResourceLocation id) {
                return ResourceKey.create(Registries.ITEM, id);
        }
}
