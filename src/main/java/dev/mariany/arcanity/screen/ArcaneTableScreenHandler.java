package dev.mariany.arcanity.screen;

import com.google.common.collect.ImmutableMap;
import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.block.ArcanityBlocks;
import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanity.enchantment.EnchantmentEntry;
import dev.mariany.arcanity.enchantment.EnchantmentProgression;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionHandler;
import dev.mariany.arcanity.enchantment.EnchantmentProgressionState;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.*;

public class ArcaneTableScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final World world;
    private final Slot inputSlot;
    private final Map<RegistryKey<Enchantment>, EnchantmentProgression> availableEnchantments = new HashMap<>();
    private final Inventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            ArcaneTableScreenHandler.this.onContentChanged(this);
            ArcaneTableScreenHandler.this.contentsChangedListener.run();
        }
    };

    private ItemStack inputStack = ItemStack.EMPTY;
    private Runnable contentsChangedListener = () -> {
    };

    public ArcaneTableScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public ArcaneTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ArcanityScreenHandlers.ARCANE_TABLE, syncId);
        this.context = context;
        this.world = playerInventory.player.getEntityWorld();
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 17, 47) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        this.addPlayerSlots(playerInventory, 8, 108);
    }

    public ItemStack getInputStack() {
        return this.inputStack;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    public ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> getStackEnchantments() {
        return EnchantmentProgressionHandler.getEnchantmentProgress(this.inputStack);
    }

    public boolean isCompatible(RegistryKey<Enchantment> candidate) {
        Map<RegistryKey<Enchantment>, EnchantmentProgression> existing = new HashMap<>(getStackEnchantments());

        existing.remove(candidate);

        return this.isCompatible(existing, candidate);
    }

    public boolean isCompatible(
            Map<RegistryKey<Enchantment>, EnchantmentProgression> existing,
            RegistryKey<Enchantment> candidate
    ) {
        return this.world
                .getRegistryManager()
                .getOptional(RegistryKeys.ENCHANTMENT)
                .map(enchantmentRegistry -> {
                    List<RegistryEntry<Enchantment>> enchantments = existing
                            .entrySet()
                            .stream()
                            .map(entry ->
                                         (RegistryEntry<Enchantment>) (entry.getValue().isEnabled() ?
                                                 enchantmentRegistry
                                                         .getOptional(entry.getKey())
                                                         .orElse(null) : null)
                            )
                            .filter(Objects::nonNull)
                            .toList();

                    return enchantmentRegistry
                            .getOptional(candidate)
                            .map(enchantment -> EnchantmentHelper.isCompatible(
                                         enchantments,
                                         enchantment
                                 )
                            )
                            .orElse(false);
                })
                .orElse(false);
    }

    public List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedAvailableEnchantments() {
        return EnchantmentProgressionComponent.getSortedEntries(
                this.world.getRegistryManager(),
                this.getAvailableEnchantments()
        );
    }

    private Map<RegistryKey<Enchantment>, EnchantmentProgression> getAvailableEnchantments() {
        return Map.copyOf(this.availableEnchantments);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ArcanityBlocks.ARCANE_TABLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> sortedAvailableEnchantments =
                this.getSortedAvailableEnchantments();

        if (sortedAvailableEnchantments.size() > id) {
            Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry =
                    sortedAvailableEnchantments.get(id);

            if (this.isCompatible(entry.getKey())) {
                ItemStack stack = this.inputStack.copy();

                applyProgress(
                        player.getRegistryManager(),
                        entry.getKey(),
                        entry.getValue().withToggledState(),
                        stack
                );

                this.inputSlot.setStack(stack);

                return true;
            }
        }

        return false;
    }

    private static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            RegistryKey<Enchantment> enchantmentKey,
            EnchantmentProgression progress,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                ArcanityComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression =
                new HashMap<>(enchantmentProgressionComponent.enchantments());

        progression.put(enchantmentKey, progress);

        EnchantmentProgressionHandler.applyProgress(dynamicRegistryManager, progression, stack);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();

        if (slotIndex == 0) {
            if (!this.insertItem(stack, 1, 37, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (this.inputSlot.hasStack() || !this.inputSlot.canInsert(stack)) {
                return ItemStack.EMPTY;
            }

            ItemStack movedStack = stack.copyWithCount(1);
            stack.decrement(1);

            this.inputSlot.setStack(movedStack);
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (stack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, stack);

        return result;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        this.inputStack = itemStack.copy();
        this.updateInput(itemStack);
    }

    private void updateInput(ItemStack stack) {
        this.availableEnchantments.clear();

        if (stack.isEmpty()) {
            return;
        }

        if (!EnchantmentProgressionHandler.hasEnchantmentProgress(stack) && stack.hasEnchantments()) {
            return;
        }

        DynamicRegistryManager registryManager = this.world.getRegistryManager();
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        for (Map.Entry<RegistryKey<Enchantment>, Enchantment> entry : enchantmentRegistry.getEntrySet()) {
            EnchantmentEntry enchantmentEntry = new EnchantmentEntry(entry);

            if (isAcceptable(registryManager, stack, enchantmentEntry)) {
                this.availableEnchantments.put(
                        enchantmentEntry.id(),
                        new EnchantmentProgression(
                                0,
                                0,
                                EnchantmentProgressionState.UNSET
                        )
                );
            }
        }

        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                ArcanityComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        this.availableEnchantments.putAll(enchantmentProgressionComponent.enchantments());
    }

    private static boolean isAcceptable(
            DynamicRegistryManager dynamicRegistryManager,
            ItemStack stack,
            EnchantmentEntry enchantmentEntry
    ) {
        RegistryKey<Enchantment> enchantmentRegistryKey = enchantmentEntry.id();
        Enchantment enchantment = enchantmentEntry.value();

        Registry<Enchantment> enchantmentRegistry = dynamicRegistryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        if (isCurse(enchantmentRegistry, enchantmentRegistryKey)) {
            return false;
        }

        if (!Arcanity.CONFIG.getConfig().arcaneTable.treasureEnchantments) {
            if (isTreasure(enchantmentRegistry, enchantmentRegistryKey)) {
                return false;
            }
        }

        if (stack.isIn(ArcanityTags.Items.MULTI_TOOL)) {
            if (isMultiToolIncompatible(enchantmentRegistry, enchantmentRegistryKey)) {
                return false;
            }
        }

        return enchantment.isAcceptableItem(stack);
    }

    private static boolean isMultiToolIncompatible(
            Registry<Enchantment> enchantmentRegistry,
            RegistryKey<Enchantment> enchantmentRegistryKey
    ) {
        return isInTag(enchantmentRegistry, enchantmentRegistryKey, ArcanityTags.Enchantments.MULTI_TOOL_INCOMPATIBLE);
    }

    private static boolean isCurse(
            Registry<Enchantment> enchantmentRegistry,
            RegistryKey<Enchantment> enchantmentRegistryKey
    ) {
        return isInTag(enchantmentRegistry, enchantmentRegistryKey, EnchantmentTags.CURSE);
    }

    private static boolean isTreasure(
            Registry<Enchantment> enchantmentRegistry,
            RegistryKey<Enchantment> enchantmentRegistryKey
    ) {
        return isInTag(enchantmentRegistry, enchantmentRegistryKey, EnchantmentTags.TREASURE);
    }

    private static boolean isInTag(
            Registry<Enchantment> enchantmentRegistry,
            RegistryKey<Enchantment> enchantmentRegistryKey,
            TagKey<Enchantment> tagKey
    ) {
        return enchantmentRegistry
                .getOptional(tagKey)
                .map(entries -> containsEnchantmentKey(entries, enchantmentRegistryKey))
                .orElse(false);
    }

    private static boolean containsEnchantmentKey(
            RegistryEntryList<Enchantment> enchantmentEntryList,
            RegistryKey<Enchantment> enchantmentRegistryKey
    ) {
        return enchantmentEntryList
                .stream()
                .anyMatch(entry -> entry.matchesKey(enchantmentRegistryKey));
    }
}
