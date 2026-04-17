package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {
    /**
     * Prevents Enchantment Tooltips for items with enchantment progression.
     */
    @Inject(method = "appendTooltip", at = @At(value = "HEAD"), cancellable = true)
    public void injectAppendTooltip(
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            ComponentsAccess components,
            CallbackInfo ci
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = components.getOrDefault(
                ArcanityComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        if (!enchantmentProgressionComponent.isEmpty()) {
            ci.cancel();
        }
    }
}
