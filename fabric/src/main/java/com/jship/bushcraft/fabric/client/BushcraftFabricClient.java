package com.jship.bushcraft.fabric.client;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.client.renderer.ChipperGeoRenderer;
import com.jship.bushcraft.client.renderer.CrucibleRenderer;
import com.jship.bushcraft.client.renderer.DryingRackRenderer;
import com.jship.bushcraft.client.renderer.HandPumpGeoRenderer;
import com.jship.bushcraft.client.renderer.TreeTapRenderer;
import com.jship.bushcraft.client.renderer.WasherGeoRenderer;
import com.jship.bushcraft.client.screen.WasherScreen;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModFluids;
import com.jship.bushcraft.init.ModMenus;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

public final class BushcraftFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Bushcraft.clientInit();

        MenuScreens.register(ModMenus.WASHER.get(), WasherScreen::new);
        // MenuScreens.register(ModMenus.CHIPPER.get(), ChipperScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PITCH_BLOCK.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ModFluids.BIRCH_SAP.get(),
                ModFluids.BIRCH_SAP_FLOWING.get(), ModFluids.SPRUCE_SAP.get(), ModFluids.SPRUCE_SAP_FLOWING.get());

        BlockEntityRendererRegistry.register(ModBlockEntities.CHIPPER.get(), ChipperGeoRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.CRUCIBLE.get(), CrucibleRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HAND_PUMP.get(), HandPumpGeoRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.TREE_TAP.get(), TreeTapRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.WASHER.get(), WasherGeoRenderer::new);
    }
}
