package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ArcanityBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ArcanityBlockTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ArcanityTags.Blocks.ARCANE_MINEABLE)
                .addOptionalTag(BlockTags.SHOVEL_MINEABLE)
                .addOptionalTag(BlockTags.PICKAXE_MINEABLE)
                .addOptionalTag(BlockTags.AXE_MINEABLE)
                .addOptionalTag(BlockTags.HOE_MINEABLE)
                .addOptionalTag(BlockTags.LEAVES)
                .addOptionalTag(BlockTags.WOOL)
                .add(Blocks.VINE)
                .add(Blocks.GLOW_LICHEN);

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(ArcanityBlocks.ARCANE_TABLE);
        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(ArcanityBlocks.ARCANE_TABLE);
    }
}