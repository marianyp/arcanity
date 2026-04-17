package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(at = @At("RETURN"), method = "getTooltipData", cancellable = true)
    public void injectGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> cir) {
        Optional<TooltipData> tooltipData = cir.getReturnValue();

        if (tooltipData.isPresent()) {
            return;
        }

        ItemStack stack = (ItemStack) (Object) this;

        Optional<TooltipData> enchantmentProgressionTooltipData = EnchantmentProgressionHandler.getTooltipData(stack);

        if (enchantmentProgressionTooltipData.isPresent()) {
            cir.setReturnValue(enchantmentProgressionTooltipData);
        }
    }
}
