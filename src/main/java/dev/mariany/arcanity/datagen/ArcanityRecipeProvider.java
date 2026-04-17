package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.block.ArcanityBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ArcanityRecipeProvider extends FabricRecipeProvider {
    public ArcanityRecipeProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(
            RegistryWrapper.WrapperLookup wrapperLookup,
            RecipeExporter recipeExporter
    ) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                this.createShapeless(RecipeCategory.MISC, ArcanityBlocks.ARCANE_TABLE, 2)
                    .input(ArcanityBlocks.ARCANE_TABLE)
                    .input(Items.CRAFTING_TABLE)
                    .criterion(hasItem(ArcanityBlocks.ARCANE_TABLE), this.conditionsFromItem(ArcanityBlocks.ARCANE_TABLE))
                    .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Arcane Hand Recipes";
    }
}
