package dev.mariany.arcanity.component;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;

public final class ArcanityComponents {
    public static final ComponentType<EnchantmentProgressionComponent> ENCHANTMENT_PROGRESSION = register(
            "enchantment_progression",
            ComponentType.<EnchantmentProgressionComponent>builder()
                         .codec(EnchantmentProgressionComponent.CODEC)
                         .packetCodec(EnchantmentProgressionComponent.PACKET_CODEC)
                         .cache()
    );

    public static final ComponentType<UUID> OWNER = register(
            "owner",
            ComponentType.<UUID>builder()
                         .codec(Uuids.CODEC)
                         .packetCodec(Uuids.PACKET_CODEC)
                         .cache()
    );

    private ArcanityComponents() {
    }

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Arcanity.id(name), builder.build());
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Components");

        ComponentTooltipAppenderRegistry.addBefore(DataComponentTypes.ENCHANTMENTS, ENCHANTMENT_PROGRESSION);
    }
}
