package dev.mariany.arcanity.stat;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class ArcanityStats {
    public static final Identifier INTERACT_WITH_ARCANE_TABLE = register(
            "interact_with_arcane_table",
            StatFormatter.DEFAULT
    );

    private ArcanityStats() {
    }

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = Arcanity.id(id);
        Registry.register(Registries.CUSTOM_STAT, id, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Stats");
    }
}
