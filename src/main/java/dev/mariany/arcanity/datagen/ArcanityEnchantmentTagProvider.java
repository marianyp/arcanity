package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.enchantment.ArcanityEnchantments;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.tag.EnchantmentTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class ArcanityEnchantmentTagProvider extends EnchantmentTagProvider {
    public ArcanityEnchantmentTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder(EnchantmentTags.SMELTS_LOOT).add(ArcanityEnchantments.BLAZE);

        this.builder(ArcanityTags.Enchantments.ABUNDANCE)
            .add(Enchantments.FORTUNE)
            .add(Enchantments.LOOTING);

        this.builder(ArcanityTags.Enchantments.ABUNDANCE_EXCLUSIVE_SET)
            .addOptionalTag(EnchantmentTags.MINING_EXCLUSIVE_SET)
            .add(ArcanityEnchantments.ABUNDANCE)
            .add(Enchantments.LOOTING);

        this.builder(ArcanityTags.Enchantments.BLAZE_EXCLUSIVE_SET)
            .add(ArcanityEnchantments.BLAZE)
            .add(Enchantments.FIRE_ASPECT);

        this.builder(ArcanityTags.Enchantments.MULTI_MINING_EXCLUSIVE_SET)
            .add(ArcanityEnchantments.EXCAVATE)
            .add(ArcanityEnchantments.VEIN_MINING);

        this.builder(ArcanityTags.Enchantments.MULTI_TOOL_INCOMPATIBLE)
            .add(Enchantments.FIRE_ASPECT)
            .add(Enchantments.FORTUNE)
            .add(Enchantments.LOOTING);
    }
}