package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.entity.CopperBellBlockEntity;
import com.jship.bushcraft.block.entity.CrucibleBlockEntity;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.jship.bushcraft.block.entity.HandPumpGeoBlockEntity;
import com.jship.bushcraft.block.entity.TreeTapBlockEntity;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = Bushcraft.MANAGER.get()
            .get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<CopperBellBlockEntity>> COPPER_BELL = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("copper_bell"), () -> BlockEntityType.Builder
                    .of(CopperBellBlockEntity::new, ModBlocks.COPPER_BELL.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("crucible"), () -> BlockEntityType.Builder
                    .of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("drying_rack"), () -> BlockEntityType.Builder
                    .of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<HandPumpGeoBlockEntity>> HAND_PUMP = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("hand_pump"), () -> BlockEntityType.Builder
                    .of(HandPumpGeoBlockEntity::new, ModBlocks.HAND_PUMP.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<TreeTapBlockEntity>> TREE_TAP = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("tree_tap"), () -> BlockEntityType.Builder
                    .of(TreeTapBlockEntity::new, ModBlocks.TREE_TAP.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WasherGeoBlockEntity>> WASHER = BLOCK_ENTITY_TYPES
            .register(Bushcraft.id("washer"), () -> BlockEntityType.Builder
                    .of(WasherGeoBlockEntity::new, ModBlocks.WASHER.get()).build(null));

    public static void init() {
    }
}
