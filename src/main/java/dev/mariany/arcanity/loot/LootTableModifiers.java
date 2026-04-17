package dev.mariany.arcanity.loot;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.config.ArcanityServerConfig;
import dev.mariany.arcanity.item.ArcanityItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableModifiers {
    private static final Map<RegistryKey<LootTable>, List<ModifierConfig>> MODIFIER_CONFIG_MAP = new HashMap<>();

    static {
        registerModifier(
                LootTables.BASTION_TREASURE_CHEST,
                new ModifierConfig(
                        ArcanityBlocks.ARCANE_TABLE,
                        config -> config.arcaneTable.spawnRates.bastionTreasure
                )
        );

        registerModifier(
                LootTables.END_CITY_TREASURE_CHEST,
                new ModifierConfig(
                        ArcanityItems.ARCANE_TOOL,
                        config -> config.arcaneTool.spawnRates.endCityTreasure
                )
        );
    }

    private static void registerModifier(RegistryKey<LootTable> key, ModifierConfig... modifierConfigs) {
        MODIFIER_CONFIG_MAP.put(key, List.of(modifierConfigs));
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Loot Table Modifiers");
        LootTableEvents.MODIFY.register(LootTableModifiers::modifyLootTable);
    }

    private static void modifyLootTable(
            RegistryKey<LootTable> key,
            LootTable.Builder tableBuilder,
            LootTableSource source,
            RegistryWrapper.WrapperLookup registries
    ) {
        List<ModifierConfig> modifierConfigs = MODIFIER_CONFIG_MAP.get(key);

        if (modifierConfigs == null) {
            return;
        }

        ArcanityServerConfig config = Arcanity.CONFIG.getConfig();

        for (ModifierConfig modifierConfig : modifierConfigs) {
            float chance = Math.min(1, modifierConfig.rate.get(config));

            if (chance <= 0) {
                continue;
            }

            tableBuilder.pool(
                    LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(modifierConfig.item.asItem()))
                            .conditionally(RandomChanceLootCondition.builder(chance))
            );
        }
    }

    @FunctionalInterface
    interface RateSupplier {
        float get(ArcanityServerConfig config);
    }

    record ModifierConfig(ItemConvertible item, RateSupplier rate) {
    }
}
