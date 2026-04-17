package dev.mariany.arcanity.event;

import dev.mariany.arcanity.Arcanity;
import dev.mariany.arcanity.item.ArcaneToolItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class AttackBlockHandler {
    private AttackBlockHandler() {
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Attack Block Handler");

        AttackBlockCallback.EVENT.register(AttackBlockHandler::onAttackBlock);
    }

    private static ActionResult onAttackBlock(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockPos pos,
            Direction direction
    ) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof ArcaneToolItem && !ArcaneToolItem.canMine(world, pos, stack)) {
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
