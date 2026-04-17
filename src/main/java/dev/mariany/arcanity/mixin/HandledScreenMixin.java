package dev.mariany.arcanity.mixin;

import dev.mariany.arcanity.client.gui.tooltip.EnchantmentProgressionTooltipSubmenuHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow
    protected abstract void addTooltipSubmenuHandler(TooltipSubmenuHandler handler);

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;addTooltipSubmenuHandler(Lnet/minecraft/client/gui/tooltip/TooltipSubmenuHandler;)V"
            )
    )
    protected void injectInit(CallbackInfo ci) {
        this.addTooltipSubmenuHandler(new EnchantmentProgressionTooltipSubmenuHandler());
    }
}
