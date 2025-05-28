package com.jship.bushcraft.block.model;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.ChipperBlock;
import com.jship.bushcraft.block.entity.ChipperGeoBlockEntity;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ChipperGeoModel extends DefaultedBlockGeoModel<ChipperGeoBlockEntity> {

    public ChipperGeoModel() {
        super(Bushcraft.id("chipper"));
    }

    @Override
    public ResourceLocation getTextureResource(ChipperGeoBlockEntity blockEntity) {
        val blockState = blockEntity.getBlockState();
		return blockState.getValue(ChipperBlock.LIT) ? Bushcraft.id("textures/block/chipper_on.png") : Bushcraft.id("textures/block/chipper.png");
	}
}
