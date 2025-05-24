package com.jship.bushcraft.fabric.datagen;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BushcraftModelProvider extends FabricModelProvider {

    public BushcraftModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerators) {
        blockStateModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.COPPER_BELL.get(), Bushcraft.id("block/copper_bell")));
        blockStateModelGenerators.createNonTemplateModelBlock(ModBlocks.CRUCIBLE.get(), ModBlocks.CRUCIBLE.get());
        blockStateModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.DRYING_RACK.get(), Bushcraft.id("block/drying_rack")).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
        blockStateModelGenerators.createNonTemplateModelBlock(ModBlocks.HAND_PUMP.get(), ModBlocks.HAND_PUMP.get());
        blockStateModelGenerators.createNonTemplateModelBlock(ModBlocks.PITCH_BLOCK.get(), ModBlocks.PITCH_BLOCK.get());
        blockStateModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.TREE_TAP.get(), Bushcraft.id("block/tree_tap")).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
        blockStateModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.WASHER.get())
                .with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT,
                        Bushcraft.id("block/washer"), Bushcraft.id("block/washer_on")))
                .with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
        blockStateModelGenerators.createNonTemplateModelBlock(ModBlocks.BIRCH_SAP_SOURCE.get());
        blockStateModelGenerators.createNonTemplateModelBlock(ModBlocks.SPRUCE_SAP_SOURCE.get());
        blockStateModelGenerators.createTrivialCube(ModBlocks.FLINT_BLOCK.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(ModItems.BIRCH_SAP_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.COPPER_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FIBER_TWINE.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.GREEN_FIBER.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.PITCH_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.PITCH.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.SPRUCE_SAP_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.SYRUP_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.SYRUP_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.WOODEN_COG.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FLINT_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FLINT_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FLINT_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FLINT_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.FLINT_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }
}
