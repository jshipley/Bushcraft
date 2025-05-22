package com.jship.bushcraft.block.model;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.entity.HandPumpGeoBlockEntity;

import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class HandPumpGeoModel extends DefaultedBlockGeoModel<HandPumpGeoBlockEntity> {

    public HandPumpGeoModel() {
        super(Bushcraft.id("hand_pump"));
    }
}
