package dev.mariany.arcanity.tag;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ArcanityTags {
    private ArcanityTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> ARCANE_MINEABLE = createTag("arcane_mineable");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Arcanity.id(name));
        }
    }

    public static final class Enchantments {
        public static final TagKey<Enchantment> ABUNDANCE = createTag("abundance");
        public static final TagKey<Enchantment> ABUNDANCE_EXCLUSIVE_SET = createTag("abundance_exclusive_set");
        public static final TagKey<Enchantment> BLAZE_EXCLUSIVE_SET = createTag("blaze_exclusive_set");
        public static final TagKey<Enchantment> MULTI_MINING_EXCLUSIVE_SET = createTag("multi_mining_exclusive_set");
        public static final TagKey<Enchantment> MULTI_TOOL_INCOMPATIBLE = createTag("multi_tool_incompatible");

        private static TagKey<Enchantment> createTag(String name) {
            return TagKey.of(RegistryKeys.ENCHANTMENT, Arcanity.id(name));
        }
    }

    public static final class Items {
        public static final TagKey<Item> ARCANE_TOOL_CATALYST = createTag("arcane_tool_catalyst");
        public static final TagKey<Item> ARCANE_TOOL_MATERIALS = createTag("arcane_tool_materials");
        public static final TagKey<Item> MULTI_TOOL = createTag("multi_tool");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Arcanity.id(name));
        }
    }
}
