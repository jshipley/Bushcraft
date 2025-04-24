package com.jship.bushcraft.neoforge;

import com.jship.bushcraft.Bushcraft;
import net.neoforged.fml.common.Mod;

@Mod(Bushcraft.MOD_ID)
public final class BushcraftNeoForge {

    public BushcraftNeoForge() {
        // Run our common setup.
        Bushcraft.init();
    }
}
