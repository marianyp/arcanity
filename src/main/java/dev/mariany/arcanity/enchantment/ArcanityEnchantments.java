package dev.mariany.arcanity.enchantment;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;

public final class ArcanityEnchantments {
    public static final RegistryKey<Enchantment> ABUNDANCE = of("abundance");
    public static final RegistryKey<Enchantment> BLAZE = of("blaze");
    public static final RegistryKey<Enchantment> EXCAVATE = of("excavate");
    public static final RegistryKey<Enchantment> VEIN_MINING = of("vein_mining");
    public static final RegistryKey<Enchantment> COLLECT = of("collect");

    private ArcanityEnchantments() {
    }

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Arcanity.id(id));
    }

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Enchantment> enchantmentRegistry = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<EntityType<?>> entityRegistry = registry.getRegistryLookup(RegistryKeys.ENTITY_TYPE);
        RegistryEntryLookup<Item> itemRegistry = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registry,
                ABUNDANCE,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(ArcanityTags.Items.MULTI_TOOL),
                                           2,
                                           3,
                                           Enchantment.leveledCost(15, 9),
                                           Enchantment.leveledCost(65, 9),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   EnchantmentEffectComponentTypes.EQUIPMENT_DROPS,
                                   EnchantmentEffectTarget.ATTACKER,
                                   EnchantmentEffectTarget.VICTIM,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.01F)),
                                   EntityPropertiesLootCondition.builder(
                                           LootContext.EntityReference.ATTACKER,
                                           EntityPredicate.Builder.create().type(
                                                   EntityTypePredicate.create(entityRegistry, EntityType.PLAYER)
                                           )
                                   )
                           )
                           .exclusiveSet(enchantmentRegistry.getOrThrow(ArcanityTags.Enchantments.ABUNDANCE_EXCLUSIVE_SET))
        );

        register(
                registry,
                BLAZE,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(ArcanityTags.Items.MULTI_TOOL),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   EnchantmentEffectComponentTypes.POST_ATTACK,
                                   EnchantmentEffectTarget.ATTACKER,
                                   EnchantmentEffectTarget.VICTIM,
                                   new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.constant(8)),
                                   DamageSourcePropertiesLootCondition.builder(
                                           DamageSourcePredicate.Builder.create().isDirect(true)
                                   )
                           )
                           .addEffect(ArcanityEnchantmentEffectComponents.SMELT_DROPS)
                           .exclusiveSet(enchantmentRegistry.getOrThrow(ArcanityTags.Enchantments.BLAZE_EXCLUSIVE_SET))
        );

        register(
                registry,
                COLLECT,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(ArcanityEnchantmentEffectComponents.COLLECT)
        );

        register(
                registry,
                EXCAVATE,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   ArcanityEnchantmentEffectComponents.MINE_RADIUS,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1))
                           )
                           .exclusiveSet(enchantmentRegistry.getOrThrow(ArcanityTags.Enchantments.MULTI_MINING_EXCLUSIVE_SET))
        );

        register(
                registry,
                VEIN_MINING,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   ArcanityEnchantmentEffectComponents.VEIN_MINE,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(50))
                           )
                           .exclusiveSet(enchantmentRegistry.getOrThrow(ArcanityTags.Enchantments.MULTI_MINING_EXCLUSIVE_SET))
        );
    }

    private static void register(
            Registerable<Enchantment> registry,
            RegistryKey<Enchantment> key,
            Enchantment.Builder builder
    ) {
        registry.register(key, builder.build(key.getValue()));
    }
}
