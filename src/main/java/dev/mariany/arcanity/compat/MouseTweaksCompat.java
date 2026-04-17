package dev.mariany.arcanity.compat;

import dev.mariany.arcanity.component.ArcanityComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class MouseTweaksCompat {
    private MouseTweaksCompat() {
    }

    public static ItemStack interceptOnMouseScrolled(ItemStack stack) {
        if (stack.contains(ArcanityComponents.ENCHANTMENT_PROGRESSION)) {
            return Items.BUNDLE.getDefaultStack();
        }

        return stack;
    }
}
