package dev.mariany.arcanity.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

import java.util.Map;

public record EnchantmentEntry(RegistryKey<Enchantment> id, Enchantment value) {
    public EnchantmentEntry(Map.Entry<RegistryKey<Enchantment>, Enchantment> enchantmentEntry) {
        this(enchantmentEntry.getKey(), enchantmentEntry.getValue());
    }
}
