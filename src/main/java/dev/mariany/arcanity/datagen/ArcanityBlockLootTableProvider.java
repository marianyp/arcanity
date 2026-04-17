package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.block.ArcanityBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ArcanityBlockLootTableProvider extends FabricBlockLootTableProvider {
    public ArcanityBlockLootTableProvider(
            FabricDataOutput dataOutput,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ArcanityBlocks.ARCANE_TABLE);
    }
}
