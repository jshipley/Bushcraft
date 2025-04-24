package com.jship.bushcraft;

import com.google.common.base.Suppliers;
import com.jship.bushcraft.block.DryingRackBlock;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.jship.bushcraft.client.renderer.DryingRackRenderer;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;

public final class Bushcraft {

    public static final String MOD_ID = "bushcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);
    public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);
    public static final Registrar<RecipeType<?>> RECIPES = MANAGER.get().get(Registries.RECIPE_TYPE);
    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = MANAGER.get().get(Registries.RECIPE_SERIALIZER);
    public static final Registrar<ResourceLocation> STATS = MANAGER.get().get(Registries.CUSTOM_STAT);

    public class ModBlocks {

        public static final RegistrySupplier<Block> DRYING_RACK = registerBlock(id("drying_rack"), () -> new DryingRackBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).noOcclusion()), true);

        public static void init() {}
    }

    public class ModBlockEntities {

        public static final RegistrySupplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK = BLOCK_ENTITY_TYPES.register(id("drying_rack"), () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));

        public static void init() {}
    }

    public class ModItems {
        public static final RegistrySupplier<Item> GREEN_FIBER = ITEMS.register(id("green_fiber"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
        public static final RegistrySupplier<Item> FIBER_TWINE = ITEMS.register(id("fiber_twine"), () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS)));
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
        public static final RegistrySupplier<RecipeSerializer<DryingRecipe>> DRYING_SERIALIZER = RECIPE_SERIALIZERS.register(id("drying"), () -> new DryingRecipe.Serializer());

        public static void init() {}
    }

    public class ModStats {

        // public static final ResourceLocation INTERACT_WITH_DRYING_RACK = id("stats.interact_with_drying_rack");
        // STATS.register(INTERACT_WITH_DRYING_RACK, 3);
        public static final RegistrySupplier<ResourceLocation> INTERACT_WITH_DRYING_RACK = STATS.register(id("interact_with_drying_rack"), () -> id("interact_with_drying_rack"));

        public static void init() {
            Stats.CUSTOM.get(INTERACT_WITH_DRYING_RACK.get(), StatFormatter.DEFAULT);
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
        ModBlocks.init();
        ModBlockEntities.init();
        ModItems.init();
        ModRecipes.init();
        ModStats.init();
    }

    public static void clientInit() {
        BlockEntityRendererRegistry.register(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);
    }
}
