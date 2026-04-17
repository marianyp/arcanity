package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @WrapOperation(
            method = "onPlayerCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ExperienceOrbEntity;repairPlayerGears(Lnet/minecraft/server/network/ServerPlayerEntity;I)I"
            )
    )
    private int wrapRepairPlayerGears(
            ExperienceOrbEntity experienceOrbEntity,
            ServerPlayerEntity player,
            int amount,
            Operation<Integer> original
    ) {
        return original.call(
                experienceOrbEntity,
                player,
                EnchantmentProgressionHandler.handleExperienceCollection(player, amount)
        );
    }
}
