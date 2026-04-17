package dev.mariany.arcanity.client.gui.tooltip;

import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import dev.mariany.arcanity.packet.serverbound.EnchantmentSelectedPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public class EnchantmentProgressionTooltipSubmenuHandler implements TooltipSubmenuHandler {
    private final Scroller scroller = new Scroller();

    @Override
    public boolean isApplicableTo(Slot slot) {
        return slot.getStack().contains(ArcanityComponents.ENCHANTMENT_PROGRESSION);
    }

    @Override
    public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
        int enchantmentCount = EnchantmentProgressionHandler.getEnchantmentProgressCount(item);

        if (enchantmentCount == 0) {
            return false;
        }

        Vector2i scrollVector = this.scroller.update(horizontal, vertical);
        int scrollDirection = scrollVector.y == 0 ? -scrollVector.x : scrollVector.y;

        if (scrollDirection != 0) {
            int previousEnchantmentIndex = EnchantmentProgressionHandler.getSelectedEnchantmentIndex(item);
            int selectedEnchantmentIndex = Scroller.scrollCycling(
                    scrollDirection,
                    previousEnchantmentIndex,
                    enchantmentCount
            );

            if (previousEnchantmentIndex != selectedEnchantmentIndex) {
                this.update(item, slotId, selectedEnchantmentIndex);
            }
        }

        return true;
    }

    @Override
    public void reset(Slot slot) {
        this.reset(slot.getStack(), slot.id);
    }

    @Override
    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            this.reset(slot.getStack(), slot.id);
        }
    }

    public void reset(ItemStack stack, int slotId) {
        this.update(stack, slotId, 0);
    }

    private void update(ItemStack stack, int slotId, int selectedEnchantmentIndex) {
        if (selectedEnchantmentIndex < EnchantmentProgressionHandler.getEnchantmentProgressCount(stack)) {
            EnchantmentProgressionHandler.setSelectedEnchantmentIndex(stack, selectedEnchantmentIndex);
            ClientPlayNetworking.send(new EnchantmentSelectedPayload(slotId, selectedEnchantmentIndex));
        }
    }
}
