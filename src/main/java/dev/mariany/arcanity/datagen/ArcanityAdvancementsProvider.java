package dev.mariany.arcanity.datagen;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.advancement.criterion.ArcanityCriteria;
import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.item.ArcanityItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ArcanityAdvancementsProvider extends FabricAdvancementProvider {
    public ArcanityAdvancementsProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    @SuppressWarnings("removal")
    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry anAncientRelicAdvancementEntry = Advancement.Builder
                .create()
                .parent(Identifier.ofVanilla("story/iron_tools"))
                .display(
                        ArcanityItems.ARCANE_TOOL,
                        Text.translatable("advancements.arcanity.story.an_ancient_relic.title"),
                        Text.translatable("advancements.arcanity.story.an_ancient_relic.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("arcane_tool", InventoryChangedCriterion.Conditions.items(ArcanityItems.ARCANE_TOOL))
                .build(consumer, advancementId("story/an_ancient_relic"));

        Advancement.Builder
                .create()
                .parent(anAncientRelicAdvancementEntry)
                .display(
                        ArcanityBlocks.ARCANE_TABLE,
                        Text.translatable("advancements.arcanity.story.level_up.title"),
                        Text.translatable("advancements.arcanity.story.level_up.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "leveled_up",
                        ArcanityCriteria.LEVELED_UP.create(new TickCriterion.Conditions(Optional.empty()))
                )
                .build(consumer, advancementId("story/level_up"));
    }

    private static String advancementId(String id) {
        return Arcanity.id(id).toString();
    }
}
