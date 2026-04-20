package dev.mariany.arcanity.enchantment;

import com.google.common.collect.ImmutableMap;
import dev.mariany.arcanity.advancement.criterion.ArcanityCriteria;
import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class EnchantmentProgressionHandler {
    public static Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipDisplayComponent tooltipDisplayComponent = stack.get(DataComponentTypes.TOOLTIP_DISPLAY);

        if (tooltipDisplayComponent != null) {
            if (tooltipDisplayComponent.shouldDisplay(ArcanityComponents.ENCHANTMENT_PROGRESSION)) {
                return Optional.ofNullable(stack.get(ArcanityComponents.ENCHANTMENT_PROGRESSION));
            }
        }

        return Optional.empty();
    }

    public static ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> getEnchantmentProgress(ItemStack stack) {
        return stack
                .getOrDefault(ArcanityComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                .enchantments();
    }

    public static int getEnchantmentProgressCount(ItemStack stack) {
        return Math.toIntExact(
                EnchantmentProgressionHandler.getEnchantmentProgress(stack)
                                             .values()
                                             .stream()
                                             .filter(EnchantmentProgression::isEnabled)
                                             .count()
        );
    }

    public static void removeEnchantmentProgression(ItemStack stack) {
        if (stack.getItem().getComponents().contains(ArcanityComponents.ENCHANTMENT_PROGRESSION)) {
            stack.set(ArcanityComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT);
        } else {
            stack.remove(ArcanityComponents.ENCHANTMENT_PROGRESSION);
        }
    }

    public static boolean isValidAnvilInput(Inventory input) {
        ItemStack firstStack = input.getStack(0);
        ItemStack secondStack = input.getStack(1);
        return isValidAnvilInput(firstStack, secondStack);
    }

    private static boolean isValidAnvilInput(ItemStack firstStack, ItemStack secondStack) {
        if (firstStack.isDamageable() && firstStack.canRepairWith(secondStack)) {
            return true;
        }

        return !hasEnchantmentProgress(firstStack) && !hasEnchantmentProgress(secondStack);
    }

    public static boolean hasEnchantmentProgress(ItemStack stack) {
        return !getEnchantmentProgress(stack).isEmpty();
    }

    public static int getSelectedEnchantmentIndex(ItemStack stack) {
        return stack.getOrDefault(ArcanityComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                    .selectedEnchantment();
    }

    public static void setSelectedEnchantmentIndex(ItemStack stack, int selectedEnchantmentIndex) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.get(
                ArcanityComponents.ENCHANTMENT_PROGRESSION
        );

        if (enchantmentProgressionComponent != null) {
            stack.set(
                    ArcanityComponents.ENCHANTMENT_PROGRESSION,
                    new EnchantmentProgressionComponent(
                            enchantmentProgressionComponent.enchantments(),
                            selectedEnchantmentIndex
                    )
            );
        }
    }

    public static int handleExperienceCollection(ServerPlayerEntity player, int amount) {
        DynamicRegistryManager registryManager = player.getRegistryManager();
        Random random = player.getRandom();

        List<EquipmentSlot> slots = new ArrayList<>(EquipmentSlot.VALUES);
        Collections.shuffle(slots, random::nextLong);

        for (EquipmentSlot equipmentSlot : slots) {
            ItemStack stack = player.getEquippedStack(equipmentSlot);

            if (!canAcceptExperience(registryManager, stack)) {
                continue;
            }

            int remainder = progress(player, stack, amount);

            if (remainder > 0) {
                return handleExperienceCollection(player, remainder);
            }

            return remainder;
        }

        return amount;
    }

    private static boolean canAcceptExperience(DynamicRegistryManager registryManager, ItemStack stack) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.get(
                ArcanityComponents.ENCHANTMENT_PROGRESSION
        );

        if (enchantmentProgressionComponent == null) {
            return false;
        }

        return enchantmentProgressionComponent.canAcceptExperience(registryManager);
    }

    private static int progress(ServerPlayerEntity player, ItemStack stack, int experience) {
        ServerWorld world = player.getEntityWorld();
        DynamicRegistryManager registryManager = world.getRegistryManager();
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                ArcanityComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        if (enchantmentProgressionComponent.isEmpty()) {
            return experience;
        }

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression = new HashMap<>();

        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = new ArrayList<>(
                enchantmentProgressionComponent.enchantments().entrySet()
        );

        Collections.shuffle(entries, world.random::nextLong);

        int remaining = experience;
        int skipped = 0;

        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : entries) {
            RegistryKey<Enchantment> enchantmentKey = entry.getKey();
            EnchantmentProgression progress = entry.getValue();
            boolean enabled = progress.isEnabled();
            int level = progress.level();
            int nextLevel = level + 1;

            Optional<Enchantment> optionalEnchantment = enchantmentRegistry.getOptionalValue(enchantmentKey);

            if (optionalEnchantment.isPresent()) {
                Enchantment enchantment = optionalEnchantment.get();
                Enchantment.Definition enchantmentDefinition = enchantment.definition();

                if (!enabled || level >= enchantmentDefinition.maxLevel()) {
                    progression.put(enchantmentKey, progress);
                    ++skipped;
                } else {
                    int earnedExperience = progress.earnedExperience();
                    int cost = progress.getUpgradeCost(enchantmentDefinition);

                    int consumed;

                    if (remaining >= cost - earnedExperience) {
                        consumed = cost - earnedExperience;

                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        nextLevel,
                                        0,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );

                        ArcanityCriteria.LEVELED_UP.trigger(player);
                    } else {
                        consumed = remaining;
                        remaining = 0;

                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        level,
                                        earnedExperience + consumed,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );
                    }

                    remaining = Math.max(0, remaining - consumed);
                }
            }
        }

        applyProgress(registryManager, progression, stack);

        if (remaining > 0 && skipped < progression.size()) {
            return progress(player, stack, remaining);
        }

        return remaining;
    }

    public static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> progression,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                ArcanityComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        EnchantmentProgressionComponent progressionComponent = new EnchantmentProgressionComponent(
                progression,
                enchantmentProgressionComponent.selectedEnchantment()
        ).excludingUnset();

        stack.set(ArcanityComponents.ENCHANTMENT_PROGRESSION, progressionComponent);
        stack.set(DataComponentTypes.ENCHANTMENTS, progressionComponent.toEnchantments(dynamicRegistryManager));
    }
}
