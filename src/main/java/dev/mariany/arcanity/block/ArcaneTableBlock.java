package dev.mariany.arcanity.block;

import dev.mariany.arcanity.screen.ArcaneTableScreenHandler;
import dev.mariany.arcanity.stat.ArcanityStats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArcaneTableBlock extends Block {
    private static final Text TITLE = Text.translatable("container.arcanity.arcane_table");

    public ArcaneTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(ArcanityStats.INTERACT_WITH_ARCANE_TABLE);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new ArcaneTableScreenHandler(
                        syncId,
                        inventory,
                        ScreenHandlerContext.create(world, pos)
                ),
                TITLE
        );
    }
}
