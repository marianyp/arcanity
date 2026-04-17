package dev.mariany.arcanity.client.render;

import dev.mariany.arcanity.enchantment.logic.breaker.BlockBreaker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class BlockBreakerRenderer {
    private BlockBreakerRenderer() {
    }

    public static boolean drawBlockOutline(
            MatrixStack matrixStack,
            VertexConsumer vertexConsumer,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) {
            return false;
        }

        return BlockBreaker.getBlockBreaker(player).map(blockBreaker -> {
            if (client.crosshairTarget instanceof BlockHitResult crosshairTarget) {
                return drawBlockOutline(
                        crosshairTarget.getBlockPos(),
                        player,
                        blockBreaker,
                        matrixStack,
                        vertexConsumer,
                        cameraX,
                        cameraY,
                        cameraZ
                );
            }

            return false;
        }).orElse(false);
    }

    private static boolean drawBlockOutline(
            BlockPos crosshairPos,
            PlayerEntity player,
            BlockBreaker blockBreaker,
            MatrixStack matrixStack,
            VertexConsumer vertexConsumer,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        if (player.isSneaking()) {
            return false;
        }

        if (!BlockBreaker.canHarvest(player, crosshairPos)) {
            return false;
        }

        World world = player.getEntityWorld();
        List<BlockPos> positions = blockBreaker.collectPossiblePositions(world, player);

        if (positions.size() <= 1) {
            return false;
        }

        return drawBlockOutline(
                positions,
                world,
                crosshairPos,
                player,
                matrixStack,
                vertexConsumer,
                cameraX,
                cameraY,
                cameraZ
        );
    }

    private static boolean drawBlockOutline(
            List<BlockPos> positions,
            World world,
            BlockPos crosshairPos,
            PlayerEntity player,
            MatrixStack matrixStack,
            VertexConsumer vertexConsumer,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        List<VoxelShape> outlineShapes = new ArrayList<>();
        outlineShapes.add(VoxelShapes.empty());

        for (BlockPos position : positions) {
            if (BlockBreaker.canHarvest(player, crosshairPos)) {
                BlockPos diffPos = position.subtract(crosshairPos);
                BlockState offsetShape = world.getBlockState(position);

                if (!offsetShape.isAir()) {
                    outlineShapes.set(
                            0,
                            VoxelShapes.union(
                                    outlineShapes.getFirst(),
                                    VoxelShapes.fullCube().offset(
                                            diffPos.getX(),
                                            diffPos.getY(),
                                            diffPos.getZ()
                                    )
                            )
                    );
                }
            }
        }

        outlineShapes.forEach(shape -> VertexRendering.drawOutline(
                matrixStack,
                vertexConsumer,
                shape,
                (double) crosshairPos.getX() - cameraX,
                (double) crosshairPos.getY() - cameraY,
                (double) crosshairPos.getZ() - cameraZ,
                Colors.WHITE
        ));

        return true;
    }
}
