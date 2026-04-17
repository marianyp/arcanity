package dev.mariany.arcanity.enchantment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum EnchantmentProgressionState implements StringIdentifiable {
    ENABLED(0, "enabled"),
    DISABLED(1, "disabled"),
    UNSET(2, "unset");

    private static final IntFunction<EnchantmentProgressionState> INDEX_MAPPER = ValueLists.createIndexToValueFunction(
            EnchantmentProgressionState::getIndex,
            values(),
            ValueLists.OutOfBoundsHandling.ZERO
    );

    public static final PacketCodec<ByteBuf, EnchantmentProgressionState> PACKET_CODEC = PacketCodecs.indexed(
            INDEX_MAPPER,
            EnchantmentProgressionState::getIndex
    );

    public static final StringIdentifiable.EnumCodec<EnchantmentProgressionState> CODEC =
            StringIdentifiable.createCodec(EnchantmentProgressionState::values);

    private final int index;
    private final String type;

    EnchantmentProgressionState(int index, final String type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String asString() {
        return this.type;
    }

    public EnchantmentProgressionState toggle() {
        if (this.equals(EnchantmentProgressionState.ENABLED)) {
            return EnchantmentProgressionState.DISABLED;
        }

        return EnchantmentProgressionState.ENABLED;
    }

    public boolean isEnabled() {
        return this.equals(EnchantmentProgressionState.ENABLED);
    }

    public boolean isUnset() {
        return this.equals(EnchantmentProgressionState.UNSET);
    }
}
