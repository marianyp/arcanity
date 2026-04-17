package dev.mariany.arcanity.advancement.criterion;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ArcanityCriteria {
    public static final TickCriterion LEVELED_UP = register("leveled_up", new TickCriterion());

    private ArcanityCriteria() {
    }

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Arcanity.id(name), criterion);
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Criteria");
    }
}
