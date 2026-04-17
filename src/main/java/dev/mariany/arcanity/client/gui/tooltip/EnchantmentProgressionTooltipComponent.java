package dev.mariany.arcanity.client.gui.tooltip;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

@Environment(EnvType.CLIENT)
public class EnchantmentProgressionTooltipComponent implements TooltipComponent {
    private static final Identifier ENCHANTMENT_PROGRESS_BAR_BORDER_TEXTURE =
            Arcanity.id("container/enchantment/enchantment_progressbar_border");
    private static final Identifier ENCHANTMENT_PROGRESS_BAR_FILL_TEXTURE =
            Arcanity.id("container/enchantment/enchantment_progressbar_fill");

    protected final EnchantmentProgressionComponent enchantmentProgression;
    protected final boolean considerDisabled;
    protected final int offset;

    public EnchantmentProgressionTooltipComponent(
            EnchantmentProgressionComponent enchantmentProgression,
            boolean considerDisabled,
            int offset
    ) {
        this.enchantmentProgression = enchantmentProgression;
        this.considerDisabled = considerDisabled;
        this.offset = offset;
    }

    public int getWidth() {
        return 124;
    }

    public int getHeight() {
        return this.enchantmentProgression.anyEnabled() ? 16 : 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.getWidth();
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return this.getHeight();
    }

    private int getXMargin(int width) {
        return (width - this.getWidth() - 1) / 2;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        if (!this.enchantmentProgression.anyEnabled()) {
            return;
        }

        this.drawProgressBar(x + this.getXMargin(width), y - this.offset, textRenderer, context);
    }

    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
        drawContext.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                ENCHANTMENT_PROGRESS_BAR_FILL_TEXTURE,
                x + 1,
                y,
                this.getProgressBarFill(),
                this.getHeight() - 3
        );

        drawContext.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                ENCHANTMENT_PROGRESS_BAR_BORDER_TEXTURE,
                x,
                y,
                this.getWidth(),
                this.getHeight() - 3
        );

        drawContext.drawCenteredTextWithShadow(
                textRenderer,
                this.getProgressBarLabel(),
                x + (this.getWidth() / 2),
                y + 3,
                Colors.WHITE
        );
    }

    private Text getProgressBarLabel() {
        double progress = this.getSelectedProgress().doubleValue();
        int percentage = MathHelper.floor(progress * 100);

        if (progress > 0 && percentage <= 0) {
            percentage = 1;
        }

        return Text.of(percentage + "%");
    }

    private int getProgressBarFill() {
        int multiplier = this.getWidth() - 2;
        return MathHelper.clamp(MathHelper.multiplyFraction(this.getSelectedProgress(), multiplier), 0, multiplier);
    }

    private Fraction getSelectedProgress() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            return this.enchantmentProgression.getSelectedProgress(player.getRegistryManager(), this.considerDisabled);
        }

        return Fraction.ZERO;
    }
}
