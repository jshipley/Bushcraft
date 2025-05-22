package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;
import com.jship.bushcraft.block.model.WasherGeoModel;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WasherGeoRenderer extends GeoBlockRenderer<WasherGeoBlockEntity> {

    public WasherGeoRenderer(BlockEntityRendererProvider.Context context) {
        super(new WasherGeoModel());

        super.addRenderLayer(new WasherFluidRenderer(this));
        super.addRenderLayer(new WasherItemRenderer(this));
    }
}
