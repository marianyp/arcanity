package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.villager.VillagerHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void injectTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        Arcanity.VILLAGER_HANDLER.setOwner(itemEntity);
    }

    @WrapOperation(
            method = "onPlayerCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"
            )
    )
    public ItemStack onPlayerCollision(ItemEntity itemEntity, Operation<ItemStack> original) {
        ItemStack stack = original.call(itemEntity);
        return VillagerHandler.removeOwner(stack);
    }
}
