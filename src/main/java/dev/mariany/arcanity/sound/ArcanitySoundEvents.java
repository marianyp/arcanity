package dev.mariany.arcanity.sound;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ArcanitySoundEvents {
    public static final SoundEvent ENTITY_VILLAGER_CAST_SPELL = register("entity.villager.cast_spell");

    private static SoundEvent register(String id) {
        return register(Arcanity.id(id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Sound Events");
    }
}
