package dev.mariany.arcanity.packet.serverbound;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

public record EnchantmentSelectedPayload(int slotId, int selectedEnchantmentIndex) implements CustomPayload {
    public static final CustomPayload.Id<EnchantmentSelectedPayload> ID = new CustomPayload.Id<>(
            Arcanity.id("enchantment_selected"));
    public static final PacketCodec<RegistryByteBuf, EnchantmentSelectedPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            EnchantmentSelectedPayload::slotId,
            PacketCodecs.INTEGER,
            EnchantmentSelectedPayload::selectedEnchantmentIndex,
            EnchantmentSelectedPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void apply(ServerPlayNetworking.Context context) {
        ServerPlayerEntity serverPlayer = context.player();
        ScreenHandler screenHandler = serverPlayer.currentScreenHandler;
        DefaultedList<Slot> slots = screenHandler.slots;

        if (this.slotId >= 0 && this.slotId < slots.size()) {
            ItemStack itemStack = slots.get(this.slotId).getStack();
            EnchantmentProgressionHandler.setSelectedEnchantmentIndex(itemStack, this.selectedEnchantmentIndex);
        }
    }
}
