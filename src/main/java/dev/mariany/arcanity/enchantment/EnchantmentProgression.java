package dev.mariany.arcanity.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public record EnchantmentProgression(int level, int earnedExperience, EnchantmentProgressionState state) {
    public static final Codec<EnchantmentProgression> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.INT
                                                    .fieldOf("level")
                                                    .forGetter(EnchantmentProgression::level),
                                            Codec.INT
                                                    .fieldOf("earned_experience")
                                                    .forGetter(EnchantmentProgression::earnedExperience),
                                            EnchantmentProgressionState.CODEC
                                                    .fieldOf("state")
                                                    .forGetter(EnchantmentProgression::state)
                                    )
                                    .apply(instance, EnchantmentProgression::new)
            );

    public static final PacketCodec<RegistryByteBuf, EnchantmentProgression> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            EnchantmentProgression::level,
            PacketCodecs.VAR_INT,
            EnchantmentProgression::earnedExperience,
            EnchantmentProgressionState.PACKET_CODEC,
            EnchantmentProgression::state,
            EnchantmentProgression::new
    );

    public static Text getEnchantmentText(RegistryEntry<Enchantment> enchantment, EnchantmentProgression progress) {
        return getEnchantmentText(enchantment, progress, false);
    }

    public static Text getEnchantmentText(
            RegistryEntry<Enchantment> enchantment,
            EnchantmentProgression progress,
            boolean bold
    ) {
        int level = progress.level();
        boolean isAppliedEnchantment = progress.level() > 0;

        MutableText name;

        if (level > 0) {
            name = Enchantment.getName(enchantment, level).copy();
        } else {
            name = enchantment.value().description().copy();
        }

        Formatting color = isAppliedEnchantment ? Formatting.GRAY : Formatting.DARK_GRAY;

        Texts.setStyleIfAbsent(name, Style.EMPTY.withColor(color).withBold(bold));

        return name;
    }

    public boolean isEnabled() {
        return this.state.isEnabled();
    }

    public boolean isUnset() {
        return this.state.isUnset();
    }

    public EnchantmentProgression withToggledState() {
        EnchantmentProgressionState newState = this.state.toggle();

        if (!newState.isEnabled()) {
            if (this.level <= 0 && this.earnedExperience <= 0) {
                newState = EnchantmentProgressionState.UNSET;
            }
        }

        return new EnchantmentProgression(
                this.level,
                this.earnedExperience,
                newState
        );
    }

    public int getUpgradeCost(Enchantment.Definition definition) {
        int nextLevel = this.level + 1;
        Enchantment.Cost minCost = definition.minCost();
        int base = Math.max(minCost.base(), minCost.perLevelAboveFirst());
        int cost = Math.min(15, Math.max(base / 2, 1) * nextLevel);
        return convertLevelsToExperience(cost);
    }

    private int convertLevelsToExperience(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + level * 6);
        }

        if (level <= 31) {
            return (int) (Math.pow(level, 2) * 2.5 - 40.5 * level + 360);
        }

        return (int) (Math.pow(level, 2) * 4.5 - 162.5 * level + 2220);
    }
}
