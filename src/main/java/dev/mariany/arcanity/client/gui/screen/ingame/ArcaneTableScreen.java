package dev.mariany.arcanity.client.gui.screen.ingame;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.client.gui.tooltip.EnchantmentProgressionTooltipComponent;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanity.enchantment.EnchantmentProgression;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionState;
import dev.mariany.arcanity.screen.ArcaneTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArcaneTableScreen extends HandledScreen<ArcaneTableScreenHandler> {
    private static final Identifier BACKGROUND = Arcanity.id("textures/gui/container/arcane_table.png");

    private static final Identifier ENABLED = Arcanity.id("container/arcane_table/enabled");

    private static final Identifier OPTION = Arcanity.id("container/arcane_table/option");
    private static final Identifier OPTION_HIGHLIGHTED =
            Arcanity.id("container/arcane_table/option_highlighted");
    private static final Identifier OPTION_DISABLED =
            Arcanity.id("container/arcane_table/option_disabled");

    private static final Identifier SCROLLER = Arcanity.id("container/arcane_table/scroller");
    private static final Identifier SCROLLER_DISABLED =
            Arcanity.id("container/arcane_table/scroller_disabled");

    private static final String ELLIPSIS = "...";

    private static final int TEXT_COLOR = -9937334;
    private static final int TEXT_COLOR_HIGHLIGHTED = -128;
    private static final int TEXT_COLOR_DISABLED = 0xFF82745C;

    private static final int MAX_DISPLAYED_ENCHANTMENTS = 4;
    private static final int OPTION_WIDTH = 107;
    private static final int OPTION_HEIGHT = 18;
    private static final int ICON_WIDTH = 18;
    private static final int ICON_HEIGHT = 18;
    private static final int TEXT_RIGHT_PADDING = 4;
    private static final int TEXT_LEFT_PADDING = 2;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_TRACK_HEIGHT = 72;

    private static final int SCROLLER_TOP_LEFT_X = 156;
    private static final int SCROLLER_TOP_LEFT_Y = 19;

    private static final int OPTIONS_TOP_LEFT_X = 46;
    private static final int OPTIONS_TOP_LEFT_Y = 19;

    private boolean mouseClicked;
    private float scrollAmount;
    private int scrollOffset;

    private Item previousItem = Items.AIR;

    public ArcaneTableScreen(
            ArcaneTableScreenHandler handler,
            PlayerInventory inventory,
            Text title
    ) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        this.backgroundHeight = 190;
        this.playerInventoryTitleY = this.playerInventoryTitleY + 24;
    }

    private void onInventoryChange() {
        ItemStack stack = this.handler.getInputStack();

        if (!stack.isOf(this.previousItem)) {
            this.scrollAmount = 0;
            this.scrollOffset = 0;
        }

        this.previousItem = stack.getItem();
    }

    public int getOptionsX() {
        return this.x + OPTIONS_TOP_LEFT_X;
    }

    public int getOptionsY() {
        return this.y + OPTIONS_TOP_LEFT_Y;
    }

    public int getEndIndexExclusive() {
        return this.scrollOffset + MAX_DISPLAYED_ENCHANTMENTS;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.drawEnchantmentTooltip(context, mouseX, mouseY);
    }

    private void drawEnchantmentTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.client != null && this.client.world != null) {
            this.client.world
                    .getRegistryManager()
                    .getOptional(RegistryKeys.ENCHANTMENT)
                    .ifPresent(enchantmentRegistry -> {
                        final List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> enchantments =
                                this.handler.getSortedAvailableEnchantments();
                        final int start = this.scrollOffset;
                        final int end = Math.min(this.getEndIndexExclusive(), enchantments.size());

                        for (int i = start; i < end; i++) {
                            int row = i - start;
                            int placedY = this.getOptionsY() + row * OPTION_HEIGHT;

                            if (isOptionHighlighted(mouseX, mouseY, this.getOptionsX(), placedY)) {
                                EnchantmentProgression progress = enchantments.get(i).getValue();

                                boolean drawn = enchantmentRegistry
                                        .getOptional(enchantments.get(i).getKey())
                                        .map(enchantment -> {
                                            context.drawTooltipImmediately(
                                                    this.textRenderer,
                                                    getEnchantmentTooltip(this.textRenderer, enchantment, progress),
                                                    mouseX,
                                                    mouseY,
                                                    HoveredTooltipPositioner.INSTANCE,
                                                    null
                                            );
                                            return true;
                                        })
                                        .orElse(false);

                                if (drawn) {
                                    break;
                                }
                            }
                        }
                    });
        }
    }

    private static List<TooltipComponent> getEnchantmentTooltip(
            TextRenderer textRenderer,
            RegistryEntry<Enchantment> enchantment,
            EnchantmentProgression progress
    ) {
        List<TooltipComponent> tooltip = new ArrayList<>();

        TooltipComponent enchantmentName = TooltipComponent.of(
                EnchantmentProgression.getEnchantmentText(
                        enchantment,
                        progress
                ).asOrderedText()
        );

        tooltip.add(enchantmentName);

        enchantment.getKey().ifPresent(
                enchantmentKey -> {
                    EnchantmentProgressionTooltipComponent progressTooltip = getEnchantmentProgressionTooltipComponent(
                            enchantmentKey,
                            progress
                    );

                    List<TooltipComponent> enchantmentDescription = getEnchantmentDescription(
                            textRenderer,
                            enchantment.value(),
                            progressTooltip.getWidth(textRenderer)
                    ).stream()
                     .map(OrderedText::of)
                     .map(TooltipComponent::of)
                     .toList();

                    tooltip.addAll(enchantmentDescription);

                    tooltip.add(
                            getEnchantmentProgressionTooltipComponent(
                                    enchantmentKey,
                                    progress
                            )
                    );
                }
        );

        return tooltip;
    }

    private static List<OrderedText> getEnchantmentDescription(
            TextRenderer textRenderer,
            Enchantment enchantment,
            int width
    ) {
        Text description = getEnchantmentDescription(enchantment).copy().formatted(Formatting.DARK_GRAY);
        return textRenderer.wrapLines(description, width);
    }

    private static Text getEnchantmentDescription(Enchantment enchantment) {
        if (enchantment.description().getContent() instanceof TranslatableTextContent translatableTextContent) {
            String enchantmentKey = translatableTextContent.getKey();
            String abbreviatedDescriptionKey = enchantmentKey + ".desc";
            String descriptionKey = enchantmentKey + ".description";

            if (!I18n.hasTranslation(descriptionKey) && I18n.hasTranslation(abbreviatedDescriptionKey)) {
                descriptionKey = abbreviatedDescriptionKey;
            }

            return Text.translatableWithFallback(descriptionKey, "");
        }

        return Text.empty();
    }

    private static EnchantmentProgressionTooltipComponent getEnchantmentProgressionTooltipComponent(
            RegistryKey<Enchantment> enchantmentKey,
            EnchantmentProgression enchantmentProgression
    ) {
        EnchantmentProgressionComponent singularProgression =
                new EnchantmentProgressionComponent(
                        Map.of(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        enchantmentProgression.level(),
                                        enchantmentProgression.earnedExperience(),
                                        EnchantmentProgressionState.ENABLED
                                )
                        ),
                        0
                );

        return new EnchantmentProgressionTooltipComponent(singularProgression, false, -1);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int centerX = (this.width - this.backgroundWidth) / 2;
        int centerY = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                centerX,
                centerY,
                0,
                0,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );

        this.renderScroller(context, this.x + 156, this.y + 19);
        this.renderOptions(context, mouseX, mouseY);
    }

    private void renderScroller(DrawContext context, int x, int y) {
        int offset = Math.round(scrollAmount * (SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT));

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                this.shouldScroll() ? SCROLLER : SCROLLER_DISABLED,
                x,
                y + offset,
                SCROLLER_WIDTH,
                SCROLLER_HEIGHT
        );
    }

    private void renderOptions(DrawContext context, int mouseX, int mouseY) {
        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments =
                this.handler.getSortedAvailableEnchantments();

        this.renderOptionsBackground(context, availableEnchantments, mouseX, mouseY);
        this.renderOptionsIcon(context, availableEnchantments);
        this.renderOptionsText(context, availableEnchantments, mouseX, mouseY);
    }

    private void renderOptionsBackground(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments,
            int mouseX,
            int mouseY
    ) {
        final int x = this.getOptionsX();
        final int start = this.scrollOffset;
        final int end = Math.min(this.getEndIndexExclusive(), availableEnchantments.size());

        for (int i = start; i < end; i++) {
            int row = i - start;
            int placedY = this.getOptionsY() + row * OPTION_HEIGHT;

            Identifier texture = OPTION_DISABLED;

            if (this.handler.isCompatible(availableEnchantments.get(i).getKey())) {
                texture = isOptionHighlighted(mouseX, mouseY, x, placedY)
                        ? OPTION_HIGHLIGHTED
                        : OPTION;
            }

            context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    x,
                    placedY,
                    OPTION_WIDTH,
                    OPTION_HEIGHT
            );
        }
    }

    private void renderOptionsIcon(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments
    ) {
        final int start = this.scrollOffset;
        final int end = Math.min(this.getEndIndexExclusive(), availableEnchantments.size());

        for (int i = start; i < end; i++) {
            int row = i - start;
            int placedY = this.getOptionsY() + row * OPTION_HEIGHT;

            if (this.handler.isCompatible(availableEnchantments.get(i).getKey())) {
                if (availableEnchantments.get(i).getValue().isEnabled()) {
                    context.drawGuiTexture(
                            RenderPipelines.GUI_TEXTURED,
                            ENABLED,
                            this.getOptionsX(),
                            placedY,
                            ICON_WIDTH,
                            ICON_HEIGHT
                    );
                }
            }
        }
    }

    private void renderOptionsText(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments,
            int mouseX,
            int mouseY
    ) {
        if (this.client != null && this.client.world != null) {
            this.client.world
                    .getRegistryManager()
                    .getOptional(RegistryKeys.ENCHANTMENT)
                    .ifPresent(enchantmentRegistry -> {
                        final int start = this.scrollOffset;
                        final int end = Math.min(this.getEndIndexExclusive(), availableEnchantments.size());

                        for (int i = start; i < end; i++) {
                            final int row = i - start;

                            final int optionX = this.getOptionsX();
                            final int optionY = this.getOptionsY() + row * OPTION_HEIGHT;

                            final int textX = optionX + ICON_WIDTH + TEXT_LEFT_PADDING;
                            final int textMaxWidth = Math.max(
                                    0,
                                    OPTION_WIDTH - (ICON_WIDTH + TEXT_LEFT_PADDING) - TEXT_RIGHT_PADDING
                            );

                            RegistryKey<Enchantment> enchantmentKey = availableEnchantments.get(i).getKey();

                            enchantmentRegistry
                                    .getOptional(enchantmentKey)
                                    .ifPresent(enchantment -> {
                                        String raw = enchantment.value().description().getString();
                                        String display = raw;

                                        if (this.textRenderer.getWidth(raw) > textMaxWidth) {
                                            int allowed = textMaxWidth - this.textRenderer.getWidth(ELLIPSIS);

                                            if (allowed > 0) {
                                                display = this.textRenderer.trimToWidth(raw, allowed) + ELLIPSIS;
                                            } else {
                                                display = ELLIPSIS;
                                            }
                                        }

                                        int fontHeight = this.textRenderer.fontHeight;
                                        int textY = (optionY + (OPTION_HEIGHT - fontHeight) / 2) + 1;

                                        int color = TEXT_COLOR_DISABLED;

                                        if (this.handler.isCompatible(enchantmentKey)) {
                                            color = this.isOptionHighlighted(mouseX, mouseY, optionX, optionY)
                                                    ? TEXT_COLOR_HIGHLIGHTED
                                                    : TEXT_COLOR;
                                        }

                                        context.drawText(
                                                this.textRenderer,
                                                display,
                                                textX,
                                                textY,
                                                color,
                                                false
                                        );
                                    });
                        }
                    });
        }
    }

    private boolean isOptionHighlighted(double mouseX, double mouseY, int optionX, int optionY) {
        return pointInRect(mouseX, mouseY, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        final double mouseX = click.x();
        final double mouseY = click.y();

        final int optionsX = this.x + OPTIONS_TOP_LEFT_X;
        final int optionsY = this.y + OPTIONS_TOP_LEFT_Y;
        final int scrollerX = this.x + SCROLLER_TOP_LEFT_X;
        final int scrollerY = this.y + SCROLLER_TOP_LEFT_Y;

        this.mouseClicked = false;

        if (this.client != null && this.client.player != null && this.client.interactionManager != null) {
            final int count = this.getAvailableEnchantmentsCount();

            final int start = this.scrollOffset;
            final int end = Math.min(start + MAX_DISPLAYED_ENCHANTMENTS, count);

            for (int i = start; i < end; i++) {
                final int row = i - start;
                final int optionY = optionsY + row * OPTION_HEIGHT;

                if (this.isOptionHighlighted(mouseX, mouseY, optionsX, optionY)) {
                    if (this.handler.onButtonClick(this.client.player, i)) {
                        this.client.interactionManager.clickButton(this.handler.syncId, i);
                        this.client.getSoundManager()
                                   .play(PositionedSoundInstance.master(
                                           SoundEvents.UI_STONECUTTER_SELECT_RECIPE,
                                           1.0F
                                   ));
                    }
                    return true;
                }

                if (
                        pointInRect(
                                mouseX,
                                mouseY,
                                scrollerX,
                                scrollerY,
                                SCROLLER_WIDTH,
                                SCROLLER_TRACK_HEIGHT
                        )
                ) {
                    this.mouseClicked = true;
                    return true;
                }
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int top = this.y + SCROLLER_HEIGHT - 1;
            float mouseOffsetFromCenter = (float) click.y() - top - ((float) SCROLLER_HEIGHT / 2);
            float scrollableHeight = SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT;

            this.scrollAmount = MathHelper.clamp(mouseOffsetFromCenter / scrollableHeight, 0, 1);
            this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5F);

            return true;
        }

        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            if (this.shouldScroll()) {
                int maxScroll = this.getMaxScroll();
                float scrollStep = (float) verticalAmount / maxScroll;

                this.scrollAmount = MathHelper.clamp(this.scrollAmount - scrollStep, 0, 1);
                this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5F);
            }
        }

        return true;
    }

    private boolean shouldScroll() {
        return this.getAvailableEnchantmentsCount() > MAX_DISPLAYED_ENCHANTMENTS;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getAvailableEnchantmentsCount() - MAX_DISPLAYED_ENCHANTMENTS);
    }

    private int getAvailableEnchantmentsCount() {
        return this.handler.getSortedAvailableEnchantments().size();
    }

    private static boolean pointInRect(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }
}
