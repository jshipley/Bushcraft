package com.jship.bushcraft.client.renderer;

import com.jship.bushcraft.block.entity.HandPumpGeoBlockEntity;
import com.jship.bushcraft.block.model.HandPumpGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HandPumpGeoRenderer extends GeoBlockRenderer<HandPumpGeoBlockEntity> {

    public HandPumpGeoRenderer(BlockEntityRendererProvider.Context context) {
        super(new HandPumpGeoModel());
    }
}
