package dev.mariany.arcanity.packet.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ServerBoundPackets {
    private ServerBoundPackets() {
    }

    public static void bootstrap() {
        ServerPlayNetworking.registerGlobalReceiver(EnchantmentSelectedPayload.ID, EnchantmentSelectedPayload::apply);
    }
}
