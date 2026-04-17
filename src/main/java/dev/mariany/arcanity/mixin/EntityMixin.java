package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.enchantment.logic.drop.DropCollector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
            method = "dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void injectDropStack(
            ServerWorld world,
            ItemStack stack,
            Vec3d offset,
            CallbackInfoReturnable<ItemEntity> cir
    ) {
        Entity entity = (Entity) (Object) this;

        if (DropCollector.collectEntityDrop(entity, stack)) {
            cir.setReturnValue(null);
        }
    }
}
