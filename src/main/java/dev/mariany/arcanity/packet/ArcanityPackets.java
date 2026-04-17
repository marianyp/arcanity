package dev.mariany.arcanity.packet;

import dev.mariany.arcanity.packet.serverbound.EnchantmentSelectedPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public final class ArcanityPackets {
    private ArcanityPackets() {
    }

    public static void bootstrap() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(EnchantmentSelectedPayload.ID, EnchantmentSelectedPayload.CODEC);
    }
}
