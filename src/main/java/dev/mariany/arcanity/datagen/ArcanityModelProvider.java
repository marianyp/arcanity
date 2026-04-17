package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.item.ArcanityItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.bool.BrokenProperty;
import net.minecraft.item.Item;

public class ArcanityModelProvider extends FabricModelProvider {
    public ArcanityModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(ArcanityBlocks.ARCANE_TABLE, TexturedModel.CUBE_BOTTOM_TOP);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerArcaneTool(itemModelGenerator);
    }

    private static void registerArcaneTool(ItemModelGenerator itemModelGenerator) {
        Item item = ArcanityItems.ARCANE_TOOL;

        ItemModel.Unbaked defaultModel = ItemModels.basic(itemModelGenerator.upload(item, Models.HANDHELD));

        ItemModel.Unbaked brokenModel = ItemModels.basic(
                itemModelGenerator.registerSubModel(item, "_broken", Models.HANDHELD)
        );

        itemModelGenerator.registerCondition(item, new BrokenProperty(), brokenModel, defaultModel);
    }
}
