package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.compat.MouseTweaksCompat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Prevent MouseTweak wheel scrolling affecting enchantment progression tooltip scrolling.
 */
@Pseudo
@Mixin(targets = {"yalter.mousetweaks.Main"})
public class MouseTweaksMainMixin {
    @WrapOperation(
            method = "onMouseScrolled",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")
    )
    private static Item arcanity$injectionMouseScrolled(ItemStack stack, Operation<Item> original) {
        return original.call(MouseTweaksCompat.interceptOnMouseScrolled(stack));
    }
}
