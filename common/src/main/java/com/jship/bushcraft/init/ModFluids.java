package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class ModFluids {
    public static final Registrar<Fluid> FLUIDS = Bushcraft.MANAGER.get().get(Registries.FLUID);

    public static final ArchitecturyFluidAttributes BIRCH_SAP_FLUID_ATTRIBUTES;
    public static final RegistrySupplier<FlowingFluid> BIRCH_SAP;
    public static final RegistrySupplier<FlowingFluid> BIRCH_SAP_FLOWING;

    public static final ArchitecturyFluidAttributes SPRUCE_SAP_FLUID_ATTRIBUTES;
    public static final RegistrySupplier<FlowingFluid> SPRUCE_SAP;
    public static final RegistrySupplier<FlowingFluid> SPRUCE_SAP_FLOWING;

    static {

        BIRCH_SAP_FLUID_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(
                () -> ModFluids.BIRCH_SAP_FLOWING,
                () -> ModFluids.BIRCH_SAP)
                .density(3000)
                .viscosity(4000)
                .dropOff(2)
                .tickDelay(60)
                .color(0xf5dd90)
                .emptySound(SoundEvents.BEEHIVE_DRIP)
                .fillSound(SoundEvents.BEEHIVE_DRIP)
                .sourceTexture(Bushcraft.id("block/sap"))
                .flowingTexture(Bushcraft.id("block/sap"))
                .overlayTexture(ResourceLocation.withDefaultNamespace("block/water_overlay"))
                .blockSupplier(() -> ModBlocks.BIRCH_SAP_SOURCE)
                .bucketItemSupplier(() -> ModItems.BIRCH_SAP_BUCKET);
        BIRCH_SAP = FLUIDS.register(Bushcraft.id("birch_sap"),
                () -> new ArchitecturyFlowingFluid.Source(BIRCH_SAP_FLUID_ATTRIBUTES));
        BIRCH_SAP_FLOWING = FLUIDS.register(Bushcraft.id("birch_sap_flowing"),
                () -> new ArchitecturyFlowingFluid.Flowing(BIRCH_SAP_FLUID_ATTRIBUTES));

        SPRUCE_SAP_FLUID_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(
                () -> ModFluids.SPRUCE_SAP_FLOWING,
                () -> ModFluids.SPRUCE_SAP)
                .density(3000)
                .viscosity(4000)
                .dropOff(2)
                .tickDelay(60)
                .color(0xaed4eb)
                .emptySound(SoundEvents.BEEHIVE_DRIP)
                .fillSound(SoundEvents.BEEHIVE_DRIP)
                .sourceTexture(Bushcraft.id("block/sap"))
                .flowingTexture(Bushcraft.id("block/sap"))
                // .sourceTexture(ResourceLocation.withDefaultNamespace("block/water_still"))
                // .flowingTexture(ResourceLocation.withDefaultNamespace("block/water_flow"))
                .overlayTexture(ResourceLocation.withDefaultNamespace("block/water_overlay"))
                .blockSupplier(() -> ModBlocks.SPRUCE_SAP_SOURCE)
                .bucketItemSupplier(() -> ModItems.SPRUCE_SAP_BUCKET);
        SPRUCE_SAP = FLUIDS.register(Bushcraft.id("spruce_sap"),
                () -> new ArchitecturyFlowingFluid.Source(SPRUCE_SAP_FLUID_ATTRIBUTES));
        SPRUCE_SAP_FLOWING = FLUIDS.register(Bushcraft.id("spruce_sap_flowing"),
                () -> new ArchitecturyFlowingFluid.Flowing(SPRUCE_SAP_FLUID_ATTRIBUTES));

    }

    public static void init() {
    }
}