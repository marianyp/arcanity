package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import dev.mariany.arcanity.item.ArcaneToolItem;
import dev.mariany.arcanity.mixin.accessor.ForgingScreenHandlerAccessor;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @WrapOperation(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    public <T> T wrapItemStackSet(ItemStack stack, ComponentType<T> type, @Nullable T value, Operation<T> original) {
        if (stack.getItem() instanceof ArcaneToolItem && type == DataComponentTypes.REPAIR_COST) {
            return original.call(stack, type, 0);
        }

        return original.call(stack, type, value);
    }

    @WrapOperation(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z"
            )
    )
    public <T> boolean wrapItemStackContains(
            ItemStack stack,
            ComponentType<T> componentType,
            Operation<Boolean> original
    ) {
        if (componentType == DataComponentTypes.STORED_ENCHANTMENTS) {
            AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;
            Inventory input = ((ForgingScreenHandlerAccessor) anvilScreenHandler).arcanity$input();

            if (EnchantmentProgressionHandler.containsEnchantmentProgress(input)) {
                return false;
            }
        }

        return original.call(stack, componentType);
    }

    @WrapOperation(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/ItemEnchantmentsComponent;)V"
            )
    )
    public void wrapEnchantmentHelperSet(
            ItemStack target,
            ItemEnchantmentsComponent enchantments,
            Operation<Void> original
    ) {
        AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;

        Inventory input = ((ForgingScreenHandlerAccessor) anvilScreenHandler).arcanity$input();

        ItemStack source = input.getStack(1);

        EnchantmentProgressionHandler.mergeEnchantmentProgression(source, target);

        original.call(target, enchantments);
    }
}
