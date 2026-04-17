package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.enchantment.logic.AbundanceHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getLevel", at = @At(value = "RETURN"), cancellable = true)
    private static void injectGetLevel(
            RegistryEntry<Enchantment> enchantment,
            ItemStack stack,
            CallbackInfoReturnable<Integer> cir
    ) {
        int level = cir.getReturnValue();
        Integer abundanceLevel = AbundanceHandler.getLevel(level, enchantment, stack);

        if (abundanceLevel != null) {
            cir.setReturnValue(abundanceLevel);
        }
    }
}
