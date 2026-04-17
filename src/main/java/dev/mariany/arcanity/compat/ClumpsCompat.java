package dev.mariany.arcanity.compat;

import com.blamejared.clumps.api.events.ClumpsEvents;
import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.fabricmc.loader.api.FabricLoader;

public final class ClumpsCompat {
    private static final String MOD_ID = "clumps";

    private ClumpsCompat() {
    }

    public static void bootstrap() {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            Arcanity.bootstrapLog("Clumps Mod Compatibility");

            ClumpsEvents.VALUE_EVENT.register(valueEvent -> {
                valueEvent.setValue(
                        EnchantmentProgressionHandler.handleExperienceCollection(
                                valueEvent.getPlayer(),
                                valueEvent.getValue()
                        )
                );

                return null;
            });
        }
    }
}
