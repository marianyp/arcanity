package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.enchantment.logic.breaker.BlockBreaker;
import dev.mariany.arcanity.server.network.MiningState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin implements MiningState {
    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Unique
    private boolean isMining = false;

    @Inject(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"
            ),
            cancellable = true
    )
    private void tryBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlockBreaker.tryBreak(this.player, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean Arcanity$isMining() {
        return this.isMining;
    }

    @Override
    public void Arcanity$setIsMining(boolean mining) {
        this.isMining = mining;
    }
}
