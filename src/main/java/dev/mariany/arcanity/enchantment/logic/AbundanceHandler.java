package dev.mariany.arcanity.enchantment.logic;

import dev.mariany.arcanity.enchantment.ArcanityEnchantments;
import dev.mariany.arcanity.tag.ArcanityTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;
import java.util.Set;

public final class AbundanceHandler {
    private AbundanceHandler() {
    }

    public static Integer getLevel(int level, RegistryEntry<Enchantment> enchantment, ItemStack stack) {
        if (level <= 0 && enchantment.isIn(ArcanityTags.Enchantments.ABUNDANCE)) {
            ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(
                    DataComponentTypes.ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT
            );

            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> enchantmentEntries =
                    itemEnchantmentsComponent.getEnchantmentEntries();

            int correctLevel = 0;

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantmentEntries) {
                Optional<RegistryKey<Enchantment>> optionalKey = entry.getKey().getKey();

                if (optionalKey.isPresent()) {
                    if (optionalKey.get().getValue().equals(ArcanityEnchantments.ABUNDANCE.getValue())) {
                        correctLevel = entry.getIntValue();
                        break;
                    }
                }
            }

            if (correctLevel != 0) {
                return correctLevel;
            }
        }

        return null;
    }
}
