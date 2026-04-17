package dev.mariany.arcanity.block;

import dev.mariany.arcanity.Arcanity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class ArcanityBlocks {
    public static final Block ARCANE_TABLE = register(
            "arcane_table",
            ArcaneTableBlock::new,
            AbstractBlock.Settings.create()
                                  .mapColor(MapColor.OAK_TAN)
                                  .instrument(NoteBlockInstrument.BASS)
                                  .sounds(BlockSoundGroup.WOOD)
                                  .strength(2.5F)
    );

    private ArcanityBlocks() {
    }

    private static Block register(
            String name,
            Function<AbstractBlock.Settings, Block> factory,
            AbstractBlock.Settings settings
    ) {
        final Identifier identifier = Arcanity.id(name);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);

        return block;
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Blocks");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                       .register(entries -> entries.addAfter(Items.SMITHING_TABLE, ARCANE_TABLE));
    }
}
