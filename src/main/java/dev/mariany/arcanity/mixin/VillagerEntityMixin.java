package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Inject(at = @At("RETURN"), method = "canGather", cancellable = true)
    public void injectCanGather(ServerWorld world, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            VillagerEntity villager = (VillagerEntity) (Object) this;
            cir.setReturnValue(Arcanity.VILLAGER_HANDLER.canGather(villager, stack));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void injectTick(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        Arcanity.VILLAGER_HANDLER.onVillagerTick(villager);
    }
}
