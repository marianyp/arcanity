package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    @WrapOperation(
            method = "getOutputStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasEnchantments(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean wrapGetOutputStack(ItemStack stack, Operation<Boolean> original) {
        return EnchantmentProgressionHandler.hasEnchantmentProgress(stack) || original.call(stack);
    }

    @Inject(method = "grind", at = @At(value = "HEAD"))
    private void injectGrind(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        EnchantmentProgressionHandler.removeEnchantmentProgression(stack);
    }
}
