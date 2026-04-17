package dev.mariany.arcanity.component;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.function.UnaryOperator;

public final class ArcanityEnchantmentEffectComponents {
    public static final ComponentType<Unit> SMELT_DROPS = register(
            "smelt_drops",
            builder -> builder.codec(Unit.CODEC)
    );

    public static final ComponentType<Unit> COLLECT = register(
            "collect",
            builder -> builder.codec(Unit.CODEC)
    );

    public static final ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> MINE_RADIUS = register(
            "mine_radius",
            builder -> builder.codec(
                    EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM)
                                          .listOf()
            )
    );

    public static final ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> VEIN_MINE = register(
            "vein_mine",
            builder -> builder.codec(
                    EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM)
                                          .listOf()
            )
    );

    private ArcanityEnchantmentEffectComponents() {
    }

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
                Arcanity.id(name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Enchantment Effect Components");
    }
}
