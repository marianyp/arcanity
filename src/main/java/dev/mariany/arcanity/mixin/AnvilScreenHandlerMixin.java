package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import dev.mariany.arcanity.item.ArcaneToolItem;
import dev.mariany.arcanity.mixin.accessor.ForgingScreenHandlerAccessor;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Final
    @Shadow
    private Property levelCost;

    @WrapOperation(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    public <T> T wrapUpdateResult(ItemStack stack, ComponentType<T> type, @Nullable T value, Operation<T> original) {
        if (stack.getItem() instanceof ArcaneToolItem && type == DataComponentTypes.REPAIR_COST) {
            return original.call(stack, type, 0);
        }

        return original.call(stack, type, value);
    }

    @Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
    public void injectUpdateResult(CallbackInfo ci) {
        AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;

        Inventory input = ((ForgingScreenHandlerAccessor) anvilScreenHandler).arcanity$input();

        if (EnchantmentProgressionHandler.isValidAnvilInput(input)) {
            return;
        }

        Inventory output = ((ForgingScreenHandlerAccessor) anvilScreenHandler).arcanity$output();

        output.setStack(0, ItemStack.EMPTY);
        this.levelCost.set(0);

        ci.cancel();
    }
}
