package dev.mariany.arcanity;

import dev.mariany.arcanity.datagen.*;
import dev.mariany.arcanity.enchantment.ArcanityEnchantments;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ArcanityDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ArcanityAdvancementsProvider::new);
        pack.addProvider(ArcanityBlockLootTableProvider::new);
        pack.addProvider(ArcanityBlockTagProvider::new);
        pack.addProvider(ArcanityEnchantmentProvider::new);
        pack.addProvider(ArcanityEnchantmentTagProvider::new);
        pack.addProvider(ArcanityItemTagProvider::new);
        pack.addProvider(ArcanityModelProvider::new);
        pack.addProvider(ArcanityRecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, ArcanityEnchantments::bootstrap);
    }
}
