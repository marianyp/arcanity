package dev.mariany.arcanity.component.type;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.arcanity.enchantment.EnchantmentProgression;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public record EnchantmentProgressionComponent(
        ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> enchantments,
        int selectedEnchantment
) implements TooltipAppender, TooltipData {
    public static final EnchantmentProgressionComponent DEFAULT = new EnchantmentProgressionComponent(
            new HashMap<>(),
            0
    );

    private static final Codec<RegistryKey<Enchantment>> ENCHANTMENT_CODEC =
            RegistryKey.createCodec(RegistryKeys.ENCHANTMENT);

    private static final PacketCodec<ByteBuf, RegistryKey<Enchantment>> ENCHANTMENT_PACKET_CODEC =
            RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT);

    public static final Codec<EnchantmentProgressionComponent> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.unboundedMap(
                                                         ENCHANTMENT_CODEC,
                                                         EnchantmentProgression.CODEC
                                                 )
                                                 .fieldOf("enchantments")
                                                 .forGetter(EnchantmentProgressionComponent::enchantments),
                                            Codec.INT.optionalFieldOf("selected_enchantment", 0)
                                                     .forGetter(EnchantmentProgressionComponent::selectedEnchantment)
                                    )
                                    .apply(instance, EnchantmentProgressionComponent::new)
            );

    public static final PacketCodec<RegistryByteBuf, EnchantmentProgressionComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, ENCHANTMENT_PACKET_CODEC, EnchantmentProgression.PACKET_CODEC),
            EnchantmentProgressionComponent::enchantments,
            PacketCodecs.INTEGER,
            EnchantmentProgressionComponent::selectedEnchantment,
            EnchantmentProgressionComponent::new
    );

    public EnchantmentProgressionComponent(
            Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments,
            int selectedEnchantment
    ) {
        this(ImmutableMap.copyOf(enchantments), selectedEnchantment);
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> sortByTooltipOrder(
            RegistryWrapper.Impl<Enchantment> enchantmentRegistry,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries,
            boolean groupByProgression
    ) {
        final Map<RegistryKey<Enchantment>, Integer> orderMap = new HashMap<>();

        enchantmentRegistry.getOptional(EnchantmentTags.TOOLTIP_ORDER).ifPresent(namedList -> {
            int i = 0;

            for (RegistryEntry<Enchantment> entry : namedList) {
                Optional<RegistryKey<Enchantment>> optionalKey = entry.getKey();

                if (optionalKey.isPresent()) {
                    orderMap.put(optionalKey.get(), i++);
                }
            }
        });

        Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> comparator =
                getEntryComparator(orderMap, groupByProgression);

        return entries.stream().sorted(comparator).toList();
    }

    private static @NotNull Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getEntryComparator(
            Map<RegistryKey<Enchantment>, Integer> orderMap,
            boolean groupByProgression
    ) {
        // Put enchantments present in orderMap before non-present enchantments ones
        Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> presence =
                Comparator.comparingInt(
                        entry -> orderMap.containsKey(entry.getKey()) ? 0 : 1
                );

        // Respect the order defined in orderMap (fallback to end if not present)
        Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> tagOrder =
                Comparator.comparingInt(
                        entry -> orderMap.getOrDefault(
                                entry.getKey(),
                                Integer.MAX_VALUE
                        )
                );

        // Ensure stable ordering for enchantments not defined in orderMap using their registry id
        Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> idTiebreaker =
                Comparator.comparing(
                        entry -> entry.getKey().getValue().toString()
                );

        if (groupByProgression) {
            Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> progressionBucket =
                    Comparator.comparingInt(
                            entry -> entry.getValue().level() > 0 ? 0 : 1
                    );

            return progressionBucket
                    .thenComparing(presence)
                    .thenComparing(tagOrder)
                    .thenComparing(idTiebreaker);
        }

        return presence.thenComparing(tagOrder).thenComparing(idTiebreaker);
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedEntries(
            RegistryWrapper.WrapperLookup registries,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments
    ) {
        return getSortedEntries(registries, enchantments, false);
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedEntries(
            RegistryWrapper.WrapperLookup registries,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments,
            boolean groupByCompletion
    ) {
        return registries.getOptional(RegistryKeys.ENCHANTMENT)
                         .map(
                                 enchantmentRegistry -> sortByTooltipOrder(
                                         enchantmentRegistry,
                                         enchantments.entrySet().stream().toList(),
                                         groupByCompletion
                                 )
                         )
                         .orElse(List.of());
    }

    public boolean canAcceptExperience(DynamicRegistryManager registryManager) {
        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : this.enchantments.entrySet()) {
            RegistryKey<Enchantment> enchantmentRegistryKey = entry.getKey();
            EnchantmentProgression enchantmentProgression = entry.getValue();

            Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

            Optional<RegistryEntry.Reference<Enchantment>> optionalEnchantmentReference =
                    enchantmentRegistry.getOptional(enchantmentRegistryKey);

            int maxLevel =
                    optionalEnchantmentReference
                            .map(
                                    enchantmentReference -> enchantmentReference
                                            .value()
                                            .getMaxLevel()
                            )
                            .orElse(0);

            if (enchantmentProgression.level() < maxLevel) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return this.enchantments.isEmpty();
    }

    public boolean anyEnabled() {
        return this.enchantments.values().stream().anyMatch(EnchantmentProgression::isEnabled);
    }

    public boolean noneEnabled() {
        return !this.anyEnabled();
    }

    public int countInactive() {
        return Math.toIntExact(
                this.enchantments
                        .values()
                        .stream()
                        .filter(enchantmentProgression -> !enchantmentProgression.isEnabled())
                        .count()
        );
    }

    public ItemEnchantmentsComponent toEnchantments(DynamicRegistryManager registryManager) {
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
                ItemEnchantmentsComponent.DEFAULT
        );

        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : enchantments.entrySet()) {
            EnchantmentProgression progression = entry.getValue();

            if (progression.isEnabled()) {
                builder.set(enchantmentRegistry.getOrThrow(entry.getKey()), progression.level());
            }
        }

        return builder.build();
    }

    public EnchantmentProgressionComponent excludingUnset() {
        Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments =
                new HashMap<>(this.enchantments);

        enchantments.entrySet().removeIf(entry -> entry.getValue().isUnset());

        return new EnchantmentProgressionComponent(enchantments, this.selectedEnchantment);
    }

    public Fraction getSelectedProgress(DynamicRegistryManager registryManager, boolean strict) {
        return registryManager
                .getOptional(RegistryKeys.ENCHANTMENT)
                .flatMap(enchantmentRegistry -> {
                    List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = sortByTooltipOrder(
                            enchantmentRegistry,
                            this.enchantments.entrySet().stream().toList(),
                            true
                    ).stream().filter(entry -> !strict || entry.getValue().isEnabled()).toList();

                    return Optional
                            .of(entries)
                            .filter(enchantments -> this.selectedEnchantment < enchantments.size())
                            .map(enchantments -> enchantments.get(this.selectedEnchantment))
                            .flatMap(
                                    entry -> enchantmentRegistry
                                            .getOptional(entry.getKey())
                                            .map(enchantment -> getProgressFraction(
                                                         entry.getValue(),
                                                         enchantment.value()
                                                 )
                                            )
                            );
                })
                .orElse(Fraction.ZERO);
    }

    private Fraction getProgressFraction(EnchantmentProgression progression, Enchantment enchantment) {
        Enchantment.Definition enchantmentDefinition = enchantment.definition();

        if (progression.level() >= enchantmentDefinition.maxLevel()) {
            return Fraction.ONE;
        }

        int neededExperience = progression.getUpgradeCost(enchantmentDefinition);
        int earnedExperience = progression.earnedExperience();

        return Fraction.getFraction(earnedExperience, neededExperience);
    }

    @Override
    public void appendTooltip(
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            ComponentsAccess components
    ) {
        RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();

        if (wrapperLookup != null) {
            appendEnchantmentProgressionTooltips(wrapperLookup, textConsumer);
        }

        if (this.noneEnabled()) {
            appendDisabledTooltip(textConsumer);
        }
    }

    private void appendEnchantmentProgressionTooltips(
            RegistryWrapper.WrapperLookup wrapperLookup,
            Consumer<Text> textConsumer
    ) {
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = wrapperLookup.getOrThrow(RegistryKeys.ENCHANTMENT);

        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = this.enchantments
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isEnabled())
                .toList();

        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> sortedEntries = sortByTooltipOrder(
                enchantmentRegistry,
                entries,
                true
        );

        int totalEnchantments = sortedEntries.size();

        for (int i = 0; i < totalEnchantments; i++) {
            Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry = sortedEntries.get(i);
            RegistryKey<Enchantment> enchantmentKey = entry.getKey();
            EnchantmentProgression progress = entry.getValue();

            boolean isSelected = this.selectedEnchantment == i && totalEnchantments > 1;

            enchantmentRegistry
                    .getOptional(enchantmentKey)
                    .ifPresent(enchantment -> {
                        Text enchantmentText = EnchantmentProgression.getEnchantmentText(
                                enchantment,
                                progress,
                                isSelected
                        );

                        textConsumer.accept(enchantmentText);
                    });
        }
    }

    private void appendDisabledTooltip(Consumer<Text> textConsumer) {
        int inactiveCount = this.countInactive();

        if (inactiveCount <= 0) {
            return;
        }

        MutableText disabledEnchantmentsText;

        if (inactiveCount == 1) {
            disabledEnchantmentsText = Text.translatable(
                    "component.arcanity.enchantment_progression.disabled_enchantment"
            );
        } else {
            disabledEnchantmentsText = Text.translatable(
                    "component.arcanity.enchantment_progression.disabled_enchantments",
                    inactiveCount
            );
        }

        Texts.setStyleIfAbsent(disabledEnchantmentsText, Style.EMPTY.withColor(Formatting.GRAY));

        textConsumer.accept(disabledEnchantmentsText);
    }
}
