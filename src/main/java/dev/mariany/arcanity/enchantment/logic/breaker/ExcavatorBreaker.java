package dev.mariany.arcanity.enchantment.logic.breaker;

import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ExcavatorBreaker implements BlockBreaker {
    public static int getMineRadius(PlayerEntity player) {
        return BlockBreaker.getValue(player, ArcanityEnchantmentEffectComponents.MINE_RADIUS);
    }

    @Override
    public List<BlockPos> collectPositions(World world, PlayerEntity player) {
        int radius = getMineRadius(player);
        ArrayList<BlockPos> potentialBrokenBlocks = new ArrayList<>();

        BlockHitResult blockHitResult = BlockBreaker.getBlockHitResult(player);

        if (blockHitResult.getType().equals(HitResult.Type.BLOCK)) {
            Direction.Axis axis = blockHitResult.getSide().getAxis();
            ArrayList<Vec3i> positions = new ArrayList<>();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        positions.add(new Vec3i(x, y, z));
                    }
                }
            }

            BlockPos origin = blockHitResult.getBlockPos();

            for (Vec3i pos : positions) {
                if (axis == Direction.Axis.Y) {
                    if (pos.getY() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                } else if (axis == Direction.Axis.X) {
                    if (pos.getX() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                } else if (axis == Direction.Axis.Z) {
                    if (pos.getZ() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                }
            }
        }

        return potentialBrokenBlocks;
    }
}
