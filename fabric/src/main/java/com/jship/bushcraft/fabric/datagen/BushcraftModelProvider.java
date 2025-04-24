package com.jship.bushcraft.fabric.datagen;

import com.jship.bushcraft.Bushcraft;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class BushcraftModelProvider extends FabricModelProvider {

    public BushcraftModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerators) {
        // blockStateModelGenerators.createSimpleFlatItemModel(ModBlocks.DRYING_TABLE.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(Bushcraft.ModBlocks.DRYING_RACK.get().asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(Bushcraft.ModItems.GREEN_FIBER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerators.generateFlatItem(Bushcraft.ModItems.FIBER_TWINE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }
}
