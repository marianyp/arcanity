package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.client.render.BlockBreakerRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void injectDrawBlockOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            double cameraX,
            double cameraY,
            double cameraZ,
            OutlineRenderState state,
            int color,
            CallbackInfo ci
    ) {
        if (BlockBreakerRenderer.drawBlockOutline(matrices, vertexConsumer, cameraX, cameraY, cameraZ)) {
            ci.cancel();
        }
    }
}


