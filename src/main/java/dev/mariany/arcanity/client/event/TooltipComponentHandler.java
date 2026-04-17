package dev.mariany.arcanity.client.event;

import dev.mariany.arcanity.client.gui.tooltip.EnchantmentProgressionTooltipComponent;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

@Environment(EnvType.CLIENT)
public final class TooltipComponentHandler {
    private TooltipComponentHandler() {
    }

    public static void bootstrap() {
        TooltipComponentCallback.EVENT.register(TooltipComponentHandler::getComponent);
    }

    private static TooltipComponent getComponent(TooltipData tooltipData) {
        if (tooltipData instanceof EnchantmentProgressionComponent enchantmentProgression) {
            return new EnchantmentProgressionTooltipComponent(enchantmentProgression, true, 1);
        }

        return null;
    }
}
