package com.jship.bushcraft;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.common.base.Suppliers;
import com.jship.bushcraft.block.DryingRackBlock;
import com.jship.bushcraft.block.HandPumpBlock;
import com.jship.bushcraft.block.TreeTapBlock;
import com.jship.bushcraft.block.WasherBlock;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.jship.bushcraft.block.entity.HandPumpGeoBlockEntity;
import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
import com.jship.bushcraft.client.renderer.TreeTapBlockEntityRenderer;
import com.jship.bushcraft.menu.WasherMenu;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;
import com.mojang.logging.LogUtils;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;

public final class Bushcraft {

    public static final String MOD_ID = "bushcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<Fluid> FLUIDS = MANAGER.get().get(Registries.FLUID);
    public static final Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);
    public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);
    public static final Registrar<MenuType<?>> MENUS = MANAGER.get().get(Registries.MENU);
    public static final Registrar<RecipeType<?>> RECIPES = MANAGER.get().get(Registries.RECIPE_TYPE);
    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = MANAGER.get().get(Registries.RECIPE_SERIALIZER);
    public static final Registrar<ResourceLocation> STATS = MANAGER.get().get(Registries.CUSTOM_STAT);

    public class ModFluids {
        public static final TagKey<Fluid> C_SAPS = TagKey.create(
            Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("c", "saps"));
        public static final TagKey<Fluid> C_SPRUCE_SAP = TagKey.create(
            Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("c", "saps/spruce"));
        public static final TagKey<Fluid> C_BIRCH_SAP = TagKey.create(
            Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("c", "saps/birch"));

        public static final ArchitecturyFluidAttributes BIRCH_SAP_FLUID_ATTRIBUTES;
        public static final RegistrySupplier<FlowingFluid> BIRCH_SAP;
        public static final RegistrySupplier<FlowingFluid> BIRCH_SAP_FLOWING;
        public static final RegistrySupplier<LiquidBlock> BIRCH_SAP_SOURCE_BLOCK;
        public static final RegistrySupplier<Item> BIRCH_SAP_BUCKET;
        public static final ArchitecturyFluidAttributes SPRUCE_SAP_FLUID_ATTRIBUTES;
        public static final RegistrySupplier<FlowingFluid> SPRUCE_SAP;
        public static final RegistrySupplier<FlowingFluid> SPRUCE_SAP_FLOWING;
        public static final RegistrySupplier<LiquidBlock> SPRUCE_SAP_SOURCE_BLOCK;
        public static final RegistrySupplier<Item> SPRUCE_SAP_BUCKET;

        static {
            BIRCH_SAP_FLUID_ATTRIBUTES = sapFluidAttributes(id("block/birch_sap"), () -> ModFluids.BIRCH_SAP_FLOWING, () -> ModFluids.BIRCH_SAP, 0xf5dd90, () -> ModFluids.BIRCH_SAP_SOURCE_BLOCK, () -> ModFluids.BIRCH_SAP_BUCKET);
            BIRCH_SAP = FLUIDS.register(id("birch_sap"),
                () -> new ArchitecturyFlowingFluid.Source(BIRCH_SAP_FLUID_ATTRIBUTES));
            BIRCH_SAP_FLOWING = FLUIDS.register(id("birch_sap_flowing"),
                () -> new ArchitecturyFlowingFluid.Flowing(BIRCH_SAP_FLUID_ATTRIBUTES));
            BIRCH_SAP_SOURCE_BLOCK = registerBlock(id("birch_sap"),
                () -> new ArchitecturyLiquidBlock(BIRCH_SAP,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                        .mapColor(MapColor.DEEPSLATE)
                        .speedFactor(0.2f)
                        .jumpFactor(0.3f)
                        .sound(SoundType.HONEY_BLOCK)), false);
            BIRCH_SAP_BUCKET = ITEMS.register(id("birch_sap_bucket"),
                () -> new ArchitecturyBucketItem(BIRCH_SAP, new Item.Properties().craftRemainder(Items.BUCKET).arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));

            SPRUCE_SAP_FLUID_ATTRIBUTES = sapFluidAttributes(id("block/spruce_sap"), () -> ModFluids.SPRUCE_SAP_FLOWING, () -> ModFluids.SPRUCE_SAP, 0xaed4eb, () -> ModFluids.SPRUCE_SAP_SOURCE_BLOCK, () -> ModFluids.SPRUCE_SAP_BUCKET);
            SPRUCE_SAP = FLUIDS.register(id("spruce_sap"),
                () -> new ArchitecturyFlowingFluid.Source(SPRUCE_SAP_FLUID_ATTRIBUTES));
            SPRUCE_SAP_FLOWING = FLUIDS.register(id("spruce_sap_flowing"),
                () -> new ArchitecturyFlowingFluid.Flowing(SPRUCE_SAP_FLUID_ATTRIBUTES));
            SPRUCE_SAP_SOURCE_BLOCK = registerBlock(id("spruce_sap"),
                () -> new ArchitecturyLiquidBlock(SPRUCE_SAP,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                        .mapColor(MapColor.DEEPSLATE)
                        .speedFactor(0.2f)
                        .jumpFactor(0.3f)
                        .ignitedByLava()
                        .sound(SoundType.HONEY_BLOCK)), false);
            SPRUCE_SAP_BUCKET = ITEMS.register(id("spruce_sap_bucket"),
                () -> new ArchitecturyBucketItem(SPRUCE_SAP, new Item.Properties().craftRemainder(Items.BUCKET).arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
            
        }

        private static ArchitecturyFluidAttributes sapFluidAttributes(
                ResourceLocation texture,
                Supplier<RegistrySupplier<FlowingFluid>> flowing,
                Supplier<RegistrySupplier<FlowingFluid>> still,
                int color,
                Supplier<RegistrySupplier<? extends LiquidBlock>> source,
                Supplier<RegistrySupplier<Item>> bucket) {
            return SimpleArchitecturyFluidAttributes.ofSupplier(flowing, still)
                    .density(3000)
                    .viscosity(4000)
                    .dropOff(2)
                    .tickDelay(60)
                    .color(color)
                    .emptySound(SoundEvents.BEEHIVE_DRIP)
                    .fillSound(SoundEvents.BEEHIVE_DRIP)
                    .sourceTexture(texture)
                    .flowingTexture(texture)
                    .overlayTexture(ResourceLocation.withDefaultNamespace("block/water_overlay"))
                    .blockSupplier(source)
                    .bucketItemSupplier(bucket);
        }

        public static void init() {
            SPRUCE_SAP_SOURCE_BLOCK.listen(sap -> ((FireBlock)Blocks.FIRE).setFlammable(sap, 5, 0));
        }
    }

    public class ModBlocks {

        public static final TagKey<Block> PRODUCES_SAP = TagKey.create(Registries.BLOCK, Bushcraft.id("produces_sap"));

        public static final RegistrySupplier<Block> DRYING_RACK = registerBlock(id("drying_rack"), () -> new DryingRackBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).ignitedByLava().noOcclusion()), true);
        public static final RegistrySupplier<Block> HAND_PUMP = registerBlock(id("hand_pump"), () -> new HandPumpBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).ignitedByLava().noOcclusion()), true);
        public static final RegistrySupplier<Block> TREE_TAP = registerBlock(id("tree_tap"), () -> new TreeTapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).ignitedByLava().noOcclusion().randomTicks()), true);
        public static final RegistrySupplier<Block> WASHER = registerBlock(id("washer"), () -> new WasherBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOKER).noOcclusion()), true);
        public static final RegistrySupplier<Block> PITCH_BLOCK = registerBlock(id("pitch_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK)), true);

        public static void init() {
            DRYING_RACK.listen(rack -> {
                ((FireBlock)Blocks.FIRE).setFlammable(rack, 5, 20);
                FuelRegistry.register(300, rack);
            });
            HAND_PUMP.listen(pump -> ((FireBlock)Blocks.FIRE).setFlammable(pump, 5, 20));
            TREE_TAP.listen(tap -> {
                ((FireBlock)Blocks.FIRE).setFlammable(tap, 5, 20);
                FuelRegistry.register(300, tap);
            });
            PITCH_BLOCK.listen(pitchBlock -> {
                ((FireBlock)Blocks.FIRE).setFlammable(pitchBlock, 10, 20);
                FuelRegistry.register(6400, pitchBlock);
            });
        }
    }

    public class ModBlockEntities {

        public static final RegistrySupplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK = BLOCK_ENTITY_TYPES.register(id("drying_rack"), () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));
        public static final RegistrySupplier<BlockEntityType<HandPumpGeoBlockEntity>> HAND_PUMP = BLOCK_ENTITY_TYPES.register(id("hand_pump"), () -> BlockEntityType.Builder.of(HandPumpGeoBlockEntity::new, ModBlocks.HAND_PUMP.get()).build(null));
        public static final RegistrySupplier<BlockEntityType<TreeTapBlockEntity>> TREE_TAP = BLOCK_ENTITY_TYPES.register(id("tree_tap"), () -> BlockEntityType.Builder.of(TreeTapBlockEntity::new, ModBlocks.TREE_TAP.get()).build(null));
        public static final RegistrySupplier<BlockEntityType<WasherGeoBlockEntity>> WASHER = BLOCK_ENTITY_TYPES.register(id("washer"), () -> BlockEntityType.Builder.of(WasherGeoBlockEntity::new, ModBlocks.WASHER.get()).build(null));

        public static void init() {}
    }

    public class ModItems {

        public static final RegistrySupplier<Item> GREEN_FIBER = ITEMS.register(id("green_fiber"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> FIBER_TWINE = ITEMS.register(id("fiber_twine"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> COPPER_NUGGET = ITEMS.register(id("copper_nugget"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> WOODEN_COG = ITEMS.register(id("wooden_cog"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> PITCH_BUCKET = ITEMS.register(id("pitch_bucket"), () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> PITCH = ITEMS.register(id("pitch"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));

        public static void init() {
            WOODEN_COG.listen(cog -> FuelRegistry.register(100, cog));
            PITCH_BUCKET.listen(pitchBucket -> FuelRegistry.register(6400, pitchBucket));
            PITCH.listen(pitch -> FuelRegistry.register(1600, pitch));
        }
    }

    public class ModMenus {
        public static RegistrySupplier<MenuType<WasherMenu>> WASHER = MENUS.register(
            id("washer"),
            () -> new MenuType<WasherMenu>(
                WasherMenu::new,
                FeatureFlags.VANILLA_SET));

        public static void init() {}
    }

    public class ModRecipes {

        public static final RegistrySupplier<RecipeType<DryingRecipe>> DRYING = RECIPES.register(id("drying"), () ->
            new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return MOD_ID + ":drying";
                }
            }
        );
        public static final RegistrySupplier<RecipeSerializer<DryingRecipe>> DRYING_SERIALIZER = RECIPE_SERIALIZERS
                .register(id("drying"), DryingRecipe.Serializer::new);
        public static final RegistrySupplier<RecipeType<WashingRecipe>> WASHING = RECIPES.register(id("washing"), () ->
            new RecipeType<WashingRecipe>() {
                @Override
                public String toString() {
                    return MOD_ID + ":washing";
                }
            }
        );
        public static final RegistrySupplier<SimpleCookingSerializer<WashingRecipe>> WASHING_SERIALIZER = RECIPE_SERIALIZERS
                .register(id("washing"), () -> new SimpleCookingSerializer<WashingRecipe>(
                        WashingRecipe::new, 200));

        public static void init() {}
    }

    public class ModStats {

        // public static final ResourceLocation INTERACT_WITH_DRYING_RACK = id("stats.interact_with_drying_rack");
        // STATS.register(INTERACT_WITH_DRYING_RACK, 3);
        // public static final RegistrySupplier<ResourceLocation> INTERACT_WITH_DRYING_RACK = STATS.register(id("interact_with_drying_rack"), () -> id("interact_with_drying_rack"));

        public static void init() {
            // Stats.CUSTOM.get(INTERACT_WITH_DRYING_RACK.get(), StatFormatter.DEFAULT);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceKey<Block> blockKey(ResourceLocation id) {
        return ResourceKey.create(Registries.BLOCK, id);
    }

    public static ResourceKey<Item> itemKey(ResourceLocation id) {
        return ResourceKey.create(Registries.ITEM, id);
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(ResourceLocation id, Supplier<T> block, boolean registerItem) {
        RegistrySupplier<T> blockSupplier = BLOCKS.register(id, block);
        if (registerItem) {
            ITEMS.register(id, () -> new BlockItem(blockSupplier.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
        }
        return blockSupplier;
    }

    public static void init() {
        ModFluids.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModItems.init();
        ModRecipes.init();
        ModStats.init();
    }

    public static void clientInit() {
        BlockEntityRendererRegistry.register(ModBlockEntities.TREE_TAP.get(), TreeTapBlockEntityRenderer::new);
    }
}
