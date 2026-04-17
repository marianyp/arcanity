package dev.mariany.arcanity.villager;

import dev.mariany.arcanity.attachment.ArcanityAttachmentTypes;
import dev.mariany.arcanity.component.ArcanityComponents;
import dev.mariany.arcanity.sound.ArcanitySoundEvents;
import dev.mariany.arcanity.tag.ArcanityTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class VillagerHandler {
    private final Supplier<Item> outputSupplier;
    private final int conversionTicks;
    private final int minimumCareerLevel;
    private final int minimumReputation;
    private final int villagerGiveRange;
    private final float villagerWalkSpeed;

    public boolean enabled = true;

    public VillagerHandler(
            Supplier<Item> outputSupplier,
            int conversionTicks,
            int minimumCareerLevel,
            int minimumReputation,
            int villagerGiveRange,
            float villagerWalkSpeed
    ) {
        if (conversionTicks < 1) {
            throw new IllegalArgumentException("conversionTicks must be at least 1, got: " + conversionTicks);
        }

        this.outputSupplier = outputSupplier;
        this.conversionTicks = conversionTicks;
        this.minimumCareerLevel = minimumCareerLevel;
        this.minimumReputation = minimumReputation;
        this.villagerGiveRange = villagerGiveRange;
        this.villagerWalkSpeed = villagerWalkSpeed;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setOwner(ItemEntity itemEntity) {
        if (itemEntity.isRemoved()) {
            return;
        }

        ItemStack stack = itemEntity.getStack();

        if (!isArcaneCatalyst(stack)) {
            return;
        }

        UUID owner = this.enabled ? getOwner(itemEntity) : null;
        UUID previousOwner = stack.get(ArcanityComponents.OWNER);

        if (Objects.equals(previousOwner, owner)) {
            return;
        }

        ItemStack stackCopy = stack.copy();

        stackCopy.set(ArcanityComponents.OWNER, owner);

        itemEntity.setStack(stackCopy);
    }

    public static ItemStack removeOwner(ItemStack stack) {
        stack.remove(ArcanityComponents.OWNER);
        return stack;
    }

    public boolean canGather(VillagerEntity villager, ItemStack stack) {
        return isArcaneCatalyst(stack) && isArcaneVillager(villager) && lacksArcaneCatalyst(villager);
    }

    public void onVillagerTick(VillagerEntity villager) {
        World world = villager.getEntityWorld();

        if (world.isClient()) {
            return;
        }

        if (!isArcaneVillager(villager)) {
            return;
        }

        StackWithSlot stackWithSlot = getFirstArcaneCatalyst(villager);

        if (stackWithSlot == null) {
            return;
        }

        Entity owner = getOwner(world, stackWithSlot.stack());

        if (owner == null || !this.enabled) {
            dropItem(villager, stackWithSlot);
            return;
        }

        int updatedArcaneTicks = Math.min(this.conversionTicks, getArcaneProgress(villager) + 1);

        setArcaneProgress(villager, updatedArcaneTicks);

        if (updatedArcaneTicks == this.conversionTicks - 1) {
            villager.playSound(ArcanitySoundEvents.ENTITY_VILLAGER_CAST_SPELL);
        }

        if (updatedArcaneTicks < this.conversionTicks) {
            if (world instanceof ServerWorld serverWorld) {

                if (updatedArcaneTicks % 10 == 0) {
                    Random random = villager.getRandom();

                    int particleCount = MathHelper.nextBetween(random, 3, 10);

                    for (int i = 0; i < particleCount; i++) {
                        serverWorld.spawnParticles(
                                ParticleTypes.WITCH,
                                villager.getX() + random.nextGaussian() * 0.13F,
                                villager.getBoundingBox().maxY + 0.5 + random.nextGaussian() * 0.13F,
                                villager.getZ() + random.nextGaussian() * 0.13F,
                                1,
                                0,
                                0,
                                0,
                                0
                        );
                    }
                }
            }

            return;
        }

        Vec3d villagerPosition = villager.getEntityPos();
        Vec3d ownerPosition = owner.getEntityPos();

        if (villagerPosition.isInRange(ownerPosition, this.villagerGiveRange)) {
            villager.playCelebrateSound();

            if (owner instanceof LivingEntity livingEntity) {
                TargetUtil.lookAt(villager, livingEntity);
            }

            dropResult(villager, stackWithSlot, ownerPosition);
            setArcaneProgress(villager, 0);
        } else {
            TargetUtil.walkTowards(
                    villager,
                    BlockPos.ofFloored(ownerPosition),
                    this.villagerWalkSpeed,
                    this.villagerGiveRange
            );
        }
    }

    private boolean isArcaneCatalyst(ItemStack stack) {
        return stack.isIn(ArcanityTags.Items.ARCANE_TOOL_CATALYST) && !stack.hasEnchantments();
    }

    private boolean isArcaneVillager(VillagerEntity villager) {
        VillagerData villagerData = villager.getVillagerData();

        if (villagerData.level() < this.minimumCareerLevel) {
            return false;
        }

        return villagerData.profession().matchesKey(VillagerProfession.CLERIC);
    }

    private boolean lacksArcaneCatalyst(VillagerEntity villager) {
        return getFirstArcaneCatalyst(villager) == null;
    }

    private void dropResult(VillagerEntity villager, StackWithSlot stackWithSlot, Vec3d position) {
        TargetUtil.give(villager, this.getArcaneResult(stackWithSlot.stack()), position);
        removeStack(villager, stackWithSlot.slot());
    }

    private static void dropItem(VillagerEntity villager, StackWithSlot stackWithSlot) {
        ItemStack stack = stackWithSlot.stack().copy();

        stack.remove(ArcanityComponents.OWNER);

        villager.dropItem(stack, false, false);

        removeStack(villager, stackWithSlot.slot());
    }

    private static void removeStack(VillagerEntity villager, int slot) {
        villager.getInventory().removeStack(slot);
    }

    private ItemStack getArcaneResult(ItemStack stack) {
        ItemStack result = stack.copyComponentsToNewStack(this.outputSupplier.get(), 1);
        result.remove(ArcanityComponents.OWNER);
        return result;
    }

    @Nullable
    private static Entity getOwner(World world, ItemStack stack) {
        UUID uuid = stack.get(ArcanityComponents.OWNER);

        if (uuid == null) {
            return null;
        }

        return world.getEntity(uuid);
    }

    @Nullable
    private StackWithSlot getFirstArcaneCatalyst(VillagerEntity villager) {
        World world = villager.getEntityWorld();
        SimpleInventory simpleInventory = villager.getInventory();

        for (int i = 0; i < simpleInventory.size(); i++) {
            ItemStack stack = simpleInventory.getStack(i);

            if (!isArcaneCatalyst(stack)) {
                continue;
            }

            if (getOwner(world, stack) instanceof PlayerEntity player) {
                if (villager.getReputation(player) < this.minimumReputation) {
                    continue;
                }
            }

            return new StackWithSlot(i, stack);
        }

        return null;
    }

    @Nullable
    private static UUID getOwner(ItemEntity itemEntity) {
        Entity owner = itemEntity.getOwner();

        if (owner == null) {
            return null;
        }

        return owner.getUuid();
    }


    @SuppressWarnings("UnstableApiUsage")
    private static int getArcaneProgress(VillagerEntity villager) {
        return villager.getAttachedOrElse(ArcanityAttachmentTypes.ARCANE_PROGRESS, 0);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void setArcaneProgress(VillagerEntity villager, int progress) {
        villager.setAttached(ArcanityAttachmentTypes.ARCANE_PROGRESS, progress);
    }
}
