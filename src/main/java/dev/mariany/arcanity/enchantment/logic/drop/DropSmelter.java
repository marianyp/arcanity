package dev.mariany.arcanity.enchantment.logic.drop;

import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DropSmelter {
    private DropSmelter() {
    }

    public static List<ItemStack> smeltDrops(ServerWorld world, ItemStack tool, List<ItemStack> drops) {
        List<ItemStack> smeltedDrops = new ArrayList<>();

        if (!canSmelt(tool)) {
            return drops;
        }

        ServerRecipeManager recipeManager = world.getRecipeManager();
        DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();

        for (ItemStack drop : drops) {
            SingleStackRecipeInput input = new SingleStackRecipeInput(drop);

            Optional<RecipeEntry<SmeltingRecipe>> recipe = recipeManager.getFirstMatch(
                    RecipeType.SMELTING,
                    input,
                    world
            );

            if (recipe.isPresent()) {
                ItemStack smelted = recipe.get().value().craft(input, dynamicRegistryManager).copy();
                smelted.setCount(drop.getCount());
                smeltedDrops.add(smelted);
            } else {
                smeltedDrops.add(drop);
            }
        }

        return smeltedDrops;
    }

    private static boolean canSmelt(ItemStack stack) {
        return EnchantmentHelper.hasAnyEnchantmentsWith(stack, ArcanityEnchantmentEffectComponents.SMELT_DROPS);
    }
}
