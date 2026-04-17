package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.item.ArcanityItems;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ArcanityItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ArcanityItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ArcanityTags.Items.ARCANE_TOOL_MATERIALS).add(Items.EMERALD);

        valueLookupBuilder(ArcanityTags.Items.ARCANE_TOOL_CATALYST).add(
                Items.NETHERITE_SWORD,
                Items.NETHERITE_SHOVEL,
                Items.NETHERITE_PICKAXE,
                Items.NETHERITE_AXE,
                Items.NETHERITE_HOE
        );

        valueLookupBuilder(ArcanityTags.Items.MULTI_TOOL).add(ArcanityItems.ARCANE_TOOL);

        valueLookupBuilder(ItemTags.SWORDS).addTag(ArcanityTags.Items.MULTI_TOOL);
        valueLookupBuilder(ItemTags.SHOVELS).addTag(ArcanityTags.Items.MULTI_TOOL);
        valueLookupBuilder(ItemTags.PICKAXES).addTag(ArcanityTags.Items.MULTI_TOOL);
        valueLookupBuilder(ItemTags.AXES).addTag(ArcanityTags.Items.MULTI_TOOL);
        valueLookupBuilder(ItemTags.HOES).addTag(ArcanityTags.Items.MULTI_TOOL);
    }
}