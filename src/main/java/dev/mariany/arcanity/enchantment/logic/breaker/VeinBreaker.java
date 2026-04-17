package dev.mariany.arcanity.enchantment.logic.breaker;

import com.google.common.collect.Sets;
import dev.mariany.arcanity.component.ArcanityEnchantmentEffectComponents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VeinBreaker implements BlockBreaker {
    public static int getMaxVeinSize(PlayerEntity player) {
        return BlockBreaker.getValue(player, ArcanityEnchantmentEffectComponents.VEIN_MINE);
    }

    @Override
    public List<BlockPos> collectPositions(World world, PlayerEntity player) {
        int maxVeinSize = getMaxVeinSize(player);
        LinkedList<Pair<BlockPos, Integer>> candidates = new LinkedList<>();
        List<BlockPos> positions = new ArrayList<>();

        BlockHitResult blockHitResult = BlockBreaker.getBlockHitResult(player);

        if (blockHitResult.getType().equals(HitResult.Type.BLOCK)) {
            BlockPos startPos = blockHitResult.getBlockPos();
            Block source = world.getBlockState(startPos).getBlock();

            positions.add(startPos);

            addValidNeighbors(candidates, startPos, 1);

            Set<BlockPos> visited = Sets.newHashSet(startPos);
            int blocks = 0;

            while (!candidates.isEmpty() && blocks < maxVeinSize) {
                Pair<BlockPos, Integer> candidate = candidates.poll();

                BlockPos pos = candidate.getLeft();
                int blockDistance = candidate.getRight();

                if (!world.isInBuildLimit(pos)) {
                    return positions;
                }

                if (visited.add(pos) && canHarvest(player, pos, source)) {
                    positions.add(pos);

                    if (blockDistance < maxVeinSize) {
                        addValidNeighbors(candidates, pos, blockDistance + 1);
                    }

                    blocks++;
                }
            }
        }

        return positions;
    }

    private static boolean canHarvest(PlayerEntity player, BlockPos pos, Block source) {
        return player.getEntityWorld().getBlockState(pos).isOf(source) && BlockBreaker.canHarvest(player, pos);
    }

    private static void addValidNeighbors(
            LinkedList<Pair<BlockPos, Integer>> candidates,
            BlockPos source,
            int distance
    ) {
        BlockPos up = source.up();
        BlockPos down = source.down();

        candidates.add(new Pair<>(up, distance));
        candidates.add(new Pair<>(down, distance));

        BlockPos[] blockPositions = new BlockPos[]{up, down, source};

        for (BlockPos blockPos : blockPositions) {
            candidates.add(new Pair<>(blockPos.west(), distance));
            candidates.add(new Pair<>(blockPos.east(), distance));
            candidates.add(new Pair<>(blockPos.north(), distance));
            candidates.add(new Pair<>(blockPos.south(), distance));
            candidates.add(new Pair<>(blockPos.north().west(), distance));
            candidates.add(new Pair<>(blockPos.north().east(), distance));
            candidates.add(new Pair<>(blockPos.south().west(), distance));
            candidates.add(new Pair<>(blockPos.south().east(), distance));
        }
    }
}
