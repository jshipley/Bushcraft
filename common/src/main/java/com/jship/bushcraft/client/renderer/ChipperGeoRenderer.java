package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.entity.ChipperGeoBlockEntity;
import com.jship.bushcraft.block.model.ChipperGeoModel;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChipperGeoRenderer extends GeoBlockRenderer<ChipperGeoBlockEntity> {

    public ChipperGeoRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChipperGeoModel());

        super.addRenderLayer(new ChipperItemRenderer(this));
    }
}
