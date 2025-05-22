package com.jship.bushcraft.block.model;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.WasherBlock;
import com.jship.bushcraft.block.entity.WasherGeoBlockEntity;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class WasherGeoModel extends DefaultedBlockGeoModel<WasherGeoBlockEntity> {

    public WasherGeoModel() {
        super(Bushcraft.id("washer"));
    }

    @Override
    public ResourceLocation getTextureResource(WasherGeoBlockEntity blockEntity) {
        val blockState = blockEntity.getBlockState();
		return blockState.getValue(WasherBlock.LIT) ? Bushcraft.id("textures/block/washer_on.png") : Bushcraft.id("textures/block/washer.png");
	}
}
