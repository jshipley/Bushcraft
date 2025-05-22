package com.jship.bushcraft.client.neoforge;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.Bushcraft.ModBlockEntities;
import com.jship.bushcraft.Bushcraft.ModMenus;
import com.jship.bushcraft.client.renderer.DryingRackRenderer;
import com.jship.bushcraft.client.renderer.HandPumpGeoRenderer;
import com.jship.bushcraft.client.renderer.WasherGeoRenderer;
import com.jship.bushcraft.client.screen.WasherScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Bushcraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BushcraftClientNeoForge {

    public BushcraftClientNeoForge() {
        Bushcraft.clientInit();
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WASHER.get(), WasherScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HAND_PUMP.get(), HandPumpGeoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WASHER.get(), WasherGeoRenderer::new);
    }
}
