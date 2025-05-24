package com.jship.bushcraft.compat.jade;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.CrucibleBlock;
import com.jship.bushcraft.block.DryingRackBlock;
import com.jship.bushcraft.block.entity.CrucibleBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class BushcraftJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        Bushcraft.LOGGER.info("Registering Jade server providers");
        registration.registerBlockDataProvider(BushcraftCrucibleProvider.INSTANCE, CrucibleBlockEntity.class);

        registration.registerItemStorage(BushcraftDryingRackProvider.INSTANCE, DryingRackBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        Bushcraft.LOGGER.info("Registering Jade client providers");
        registration.registerBlockComponent(BushcraftCrucibleProvider.INSTANCE, CrucibleBlock.class);

        registration.registerItemStorageClient(BushcraftDryingRackProvider.INSTANCE);
    }
}
