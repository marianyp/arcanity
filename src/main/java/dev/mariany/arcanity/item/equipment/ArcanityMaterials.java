package dev.mariany.arcanity.item.equipment;

import dev.mariany.arcanity.tag.ArcanityTags;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public final class ArcanityMaterials {
    private ArcanityMaterials() {
    }

    public static final ToolMaterial ARCANE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            2031,
            9,
            4,
            22,
            ArcanityTags.Items.ARCANE_TOOL_MATERIALS
    );
}
