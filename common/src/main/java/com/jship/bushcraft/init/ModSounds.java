package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.val;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final Registrar<SoundEvent> SOUNDS = Bushcraft.MANAGER.get().get(Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> CHIPPER_WORKING = registerSound("chipper_working");

    public static RegistrySupplier<SoundEvent> registerSound(String name) {
        val id = Bushcraft.id(name);
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {
    }

}
