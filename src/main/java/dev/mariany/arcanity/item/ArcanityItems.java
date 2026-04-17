package dev.mariany.arcanity.item;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanity.item.equipment.ArcanityMaterials;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public final class ArcanityItems {
    public static final Item ARCANE_TOOL = register(
            "arcane_tool",
            settings -> new ArcaneToolItem(
                    new ArcaneToolItem.ToolDelegates(
                            Items.NETHERITE_SHOVEL,
                            Items.NETHERITE_AXE,
                            Items.NETHERITE_HOE
                    ),
                    settings
            ),
            new Item.Settings()
                    .tool(
                            ArcanityMaterials.ARCANE,
                            ArcanityTags.Blocks.ARCANE_MINEABLE,
                            3,
                            -2.4F,
                            0
                    )
                    .component(ArcanityComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                    .rarity(Rarity.UNCOMMON)
                    .fireproof()
    );

    private ArcanityItems() {
    }

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> itemKey = keyOf(name);
        Item item = factory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Arcanity.id(id));
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                       .register(entries -> entries.addAfter(Items.NETHERITE_HOE, ARCANE_TOOL));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                       .register(entries -> entries.addAfter(Items.MACE, ARCANE_TOOL));
    }
}
