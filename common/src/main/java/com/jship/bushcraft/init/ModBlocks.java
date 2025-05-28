package com.jship.bushcraft.init;

import java.util.function.Supplier;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.ChipperBlock;
import com.jship.bushcraft.block.CopperBellBlock;
import com.jship.bushcraft.block.CrucibleBlock;
import com.jship.bushcraft.block.DryingRackBlock;
import com.jship.bushcraft.block.FermentingBarrelBlock;
import com.jship.bushcraft.block.HandPumpBlock;
import com.jship.bushcraft.block.PitchBlock;
import com.jship.bushcraft.block.TreeTapBlock;
import com.jship.bushcraft.block.WasherBlock;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    public static final Registrar<Block> BLOCKS = Bushcraft.MANAGER.get().get(Registries.BLOCK);

    public static final RegistrySupplier<Block> CHIPPER = registerBlock(Bushcraft.id("chipper"),
            () -> new ChipperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOKER).noOcclusion()), true);
    public static final RegistrySupplier<Block> COPPER_BELL = registerBlock(Bushcraft.id("copper_bell"),
            () -> new CopperBellBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion()), true);
    public static final RegistrySupplier<Block> CRUCIBLE = registerBlock(Bushcraft.id("crucible"),
            () -> new CrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).noOcclusion()), true);
    public static final RegistrySupplier<Block> DRYING_RACK = registerBlock(Bushcraft.id("drying_rack"),
            () -> new DryingRackBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).ignitedByLava().noOcclusion()),
            true);

    public static final RegistrySupplier<Block> HAND_PUMP = registerBlock(Bushcraft.id("hand_pump"),
            () -> new HandPumpBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).ignitedByLava().noOcclusion()),
            true);
    public static final RegistrySupplier<Block> MULCH_BLOCK = registerBlock(Bushcraft.id("mulch_block"),
            () -> new Block(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).ignitedByLava()), true);
    public static final RegistrySupplier<Block> TREE_TAP = registerBlock(Bushcraft.id("tree_tap"),
            () -> new TreeTapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).ignitedByLava()
                    .noOcclusion().randomTicks()),
            true);
    public static final RegistrySupplier<Block> WASHER = registerBlock(Bushcraft.id("washer"),
            () -> new WasherBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOKER).noOcclusion()), true);
    public static final RegistrySupplier<Block> PITCH_BLOCK = registerBlock(Bushcraft.id("pitch_block"),
            () -> new PitchBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(MapColor.COLOR_BLACK)),
            true);
    public static final RegistrySupplier<? extends LiquidBlock> BIRCH_SAP_SOURCE = registerBlock(
            Bushcraft.id("birch_sap"),
            () -> new ArchitecturyLiquidBlock(ModFluids.BIRCH_SAP,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                            .mapColor(MapColor.COLOR_YELLOW)
                            .speedFactor(0.2f)
                            .jumpFactor(0.3f)
                            .sound(SoundType.HONEY_BLOCK)),
            false);
    public static final RegistrySupplier<? extends LiquidBlock> SPRUCE_SAP_SOURCE = registerBlock(
            Bushcraft.id("spruce_sap"),
            () -> new ArchitecturyLiquidBlock(ModFluids.SPRUCE_SAP,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .speedFactor(0.2f)
                            .jumpFactor(0.3f)
                            .ignitedByLava()
                            .sound(SoundType.HONEY_BLOCK)),
            false);
    public static final RegistrySupplier<Block> FLINT_BLOCK = registerBlock(Bushcraft.id("flint_block"),
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.DEEPSLATE)
                    .sound(SoundType.DEEPSLATE_TILES)),
            true);
//     public static final RegistrySupplier<Block> RITUAL_BOWL = registerBlock(Bushcraft.id("ritual_bowl"),
//             () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()), true);
//     public static final RegistrySupplier<Block> FERMENTING_BARREL = registerBlock(Bushcraft.id("fermenting_barrel"),
//             () -> new FermentingBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), true);

    public static void init() {
        DRYING_RACK.listen(rack -> {
            ((FireBlock) Blocks.FIRE).setFlammable(rack, 5, 20);
            FuelRegistry.register(300, rack);
        });
        HAND_PUMP.listen(pump -> ((FireBlock) Blocks.FIRE).setFlammable(pump, 5, 20));
        MULCH_BLOCK.listen(mulch -> {
                ((FireBlock) Blocks.FIRE).setFlammable(mulch, 5, 20);
                FuelRegistry.register(300, mulch);
        });
        PITCH_BLOCK.listen(pitchBlock -> {
            ((FireBlock) Blocks.FIRE).setFlammable(pitchBlock, 10, 0);
            FuelRegistry.register(6400, pitchBlock);
        });
        SPRUCE_SAP_SOURCE.listen(sap -> ((FireBlock) Blocks.FIRE).setFlammable(sap, 5, 0));
        TREE_TAP.listen(tap -> {
            ((FireBlock) Blocks.FIRE).setFlammable(tap, 5, 20);
            FuelRegistry.register(300, tap);
        });
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(ResourceLocation id, Supplier<T> block,
            boolean registerItem) {
        RegistrySupplier<T> blockSupplier = BLOCKS.register(id, block);
        if (registerItem) {
            ModItems.ITEMS.register(id, () -> new BlockItem(blockSupplier.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
        }
        return blockSupplier;
    }

    public static ResourceKey<Block> blockKey(ResourceLocation id) {
        return ResourceKey.create(Registries.BLOCK, id);
    }
}
