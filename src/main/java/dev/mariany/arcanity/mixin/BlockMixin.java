package dev.mariany.arcanity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanity.enchantment.logic.drop.DropCollector;
import dev.mariany.arcanity.enchantment.logic.drop.DropSmelter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private static void injectDropStacks(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        cir.setReturnValue(DropSmelter.smeltDrops(world, tool, cir.getReturnValue()));
    }

    @WrapOperation(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private static void wrapDropStacks(
            List<ItemStack> stacks,
            Consumer<ItemStack> consumer,
            Operation<Void> original,
            @Local(index = 0, argsOnly = true) BlockState state,
            @Local(index = 1, argsOnly = true) World world,
            @Local(index = 2, argsOnly = true) BlockPos pos,
            @Local(index = 3, argsOnly = true) @Nullable BlockEntity blockEntity,
            @Local(index = 4, argsOnly = true) @Nullable Entity entity,
            @Local(index = 5, argsOnly = true) ItemStack tool
    ) {
        if (!DropCollector.collectBlock(entity, tool, stacks, consumer)) {
            original.call(stacks, consumer);
        }
    }
}
