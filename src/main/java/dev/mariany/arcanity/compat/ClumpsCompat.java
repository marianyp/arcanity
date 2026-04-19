package dev.mariany.arcanity.compat;

import com.blamejared.clumps.api.events.ClumpsEvents;
import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ClumpsCompat {
    private static final String MOD_ID = "clumps";

    private ClumpsCompat() {
    }

    public static void bootstrap() {
        if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            return;
        }

        Arcanity.bootstrapLog("Clumps Mod Compatibility");

        ClumpsEvents.VALUE_EVENT.register(repairEvent -> {
            if (repairEvent.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
                repairEvent.setValue(
                        EnchantmentProgressionHandler.handleExperienceCollection(
                                serverPlayer,
                                repairEvent.getValue()
                        )
                );
            }

            return null;
        });
    }
}
