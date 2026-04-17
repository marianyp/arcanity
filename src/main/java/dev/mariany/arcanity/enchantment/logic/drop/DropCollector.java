package dev.mariany.arcanity.enchantment.logic.drop;

import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class DropCollector {
    private DropCollector() {
    }

    public static boolean collectEntityDrop(Entity entity, ItemStack stack) {
        if (!stack.isEmpty() && entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isDead() && livingEntity.getPrimeAdversary() instanceof PlayerEntity player) {
                if (canCollect(player)) {
                    collect(player, stack);
                    return stack.isEmpty();
                }
            }
        }

        return false;
    }

    public static boolean collectBlock(
            Entity entity,
            ItemStack tool,
            List<ItemStack> stacks,
            Consumer<ItemStack> consumer
    ) {
        if (entity instanceof PlayerEntity player) {
            if (canCollect(tool)) {
                collect(player, stacks, consumer);
                return true;
            }
        }

        return false;
    }

    private static boolean canCollect(LivingEntity livingEntity) {
        if (canCollect(livingEntity.getMainHandStack())) {
            return true;
        }

        return canCollect(livingEntity.getOffHandStack());
    }

    private static boolean canCollect(ItemStack stack) {
        return EnchantmentHelper.hasAnyEnchantmentsWith(stack, ArcanityEnchantmentEffectComponents.COLLECT);
    }

    private static void collect(PlayerEntity player, ItemStack stack) {
        collect(player, List.of(stack), null);
    }

    private static void collect(PlayerEntity player, List<ItemStack> stacks, @Nullable Consumer<ItemStack> consumer) {
        List<ItemStack> remainingStacks = collect(player, stacks);

        if (consumer != null) {
            remainingStacks.forEach(consumer);
        }
    }

    private static List<ItemStack> collect(PlayerEntity player, List<ItemStack> stacks) {
        List<ItemStack> stacksCopy = stacks.stream().map(ItemStack::copy).toList();

        List<ItemStack> remainingStacks = stacks
                .stream()
                .filter(stack -> !player.getInventory().insertStack(stack))
                .toList();

        boolean inserted = false;

        int originalDropCount = stacksCopy.size();
        int remainingDropCount = remainingStacks.size();

        if (originalDropCount != remainingDropCount) {
            inserted = true;
        } else {
            for (int i = 0; i < originalDropCount; i++) {
                ItemStack originalStack = stacksCopy.get(i);
                ItemStack remainingStack = remainingStacks.get(i);

                if (originalStack.getCount() != remainingStack.getCount()) {
                    inserted = true;
                    break;
                }

                if (!ItemStack.areItemsAndComponentsEqual(originalStack, remainingStack)) {
                    inserted = true;
                    break;
                }
            }
        }

        if (inserted) {
            Random random = player.getRandom();

            player.playSoundToPlayer(
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.PLAYERS,
                    0.2F,
                    (random.nextFloat() - random.nextFloat()) * 1.4F + 2
            );
        }

        return remainingStacks;
    }
}
