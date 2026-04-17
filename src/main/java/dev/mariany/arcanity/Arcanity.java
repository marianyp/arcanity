package dev.mariany.arcanity;

import dev.mariany.arcanity.advancement.criterion.ArcanityCriteria;
import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.compat.ClumpsCompat;
import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import dev.mariany.arcanity.config.ArcanityServerConfig;
import dev.mariany.arcanity.config.ConfigHandler;
import dev.mariany.arcanity.event.AttackBlockHandler;
import dev.mariany.arcanity.item.ArcanityItems;
import dev.mariany.arcanity.loot.LootTableModifiers;
import dev.mariany.arcanity.packet.ArcanityPackets;
import dev.mariany.arcanity.packet.serverbound.ServerBoundPackets;
import dev.mariany.arcanity.screen.ArcanityScreenHandlers;
import dev.mariany.arcanity.sound.ArcanitySoundEvents;
import dev.mariany.arcanity.stat.ArcanityStats;
import dev.mariany.arcanity.villager.VillagerHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Arcanity implements ModInitializer {
    public static final String MOD_ID = "arcanity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ConfigHandler<ArcanityServerConfig> CONFIG = new ConfigHandler<>(
            "arcanity-server",
            new ArcanityServerConfig(),
            LOGGER
    );

    public static final VillagerHandler VILLAGER_HANDLER = new VillagerHandler(
            () -> ArcanityItems.ARCANE_TOOL,
            70,
            2,
            -100,
            5,
            0.5F
    );

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    public static void bootstrapLog(String type) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.info("Registering {}", type);
        }
    }

    @Override
    public void onInitialize() {
        CONFIG.loadConfig();

        VILLAGER_HANDLER.setEnabled(CONFIG.getConfig().arcaneTool.fromVillager);

        bootstrapPackets();

        ArcanitySoundEvents.bootstrap();
        ArcanityStats.bootstrap();
        ArcanityCriteria.bootstrap();
        ArcanityComponents.bootstrap();
        ArcanityEnchantmentEffectComponents.bootstrap();
        ArcanityItems.bootstrap();
        ArcanityScreenHandlers.bootstrap();
        ArcanityBlocks.bootstrap();
        AttackBlockHandler.bootstrap();
        LootTableModifiers.bootstrap();
        ClumpsCompat.bootstrap();
    }

    private void bootstrapPackets() {
        ArcanityPackets.bootstrap();
        ServerBoundPackets.bootstrap();
    }
}