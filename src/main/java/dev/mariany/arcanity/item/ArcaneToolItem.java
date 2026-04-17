package dev.mariany.arcanity.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArcaneToolItem extends Item {
    private final ToolDelegates toolDelegates;

    public ArcaneToolItem(ToolDelegates toolDelegates, Settings settings) {
        super(settings);
        this.toolDelegates = toolDelegates;
    }

    public static boolean canMine(World world, BlockPos pos, ItemStack stack) {
        return !isBroken(stack) || world.getBlockState(pos).getHardness(world, pos) <= 0;
    }

    private static boolean isBroken(ItemStack stack) {
        return stack.shouldBreak() || stack.willBreakNextUse();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();

        if (isBroken(stack)) {
            return ActionResult.PASS;
        }

        if (!this.use(context)) {
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();

        if (player != null) {
            stack.damage(1, player, context.getHand().getEquipmentSlot());
        }

        return ActionResult.SUCCESS;
    }

    private boolean use(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();

        if (player == null || !player.isSneaking()) {
            if (useAsHoe(context)) {
                return true;
            }
        }

        return useAsShovel(context) || useAsAxe(context);
    }

    private boolean useAsShovel(ItemUsageContext context) {
        return useItem(this.toolDelegates.shovel, context);
    }

    private boolean useAsAxe(ItemUsageContext context) {
        return useItem(this.toolDelegates.axe, context);
    }

    private boolean useAsHoe(ItemUsageContext context) {
        return useItem(this.toolDelegates.hoe, context);
    }

    private static boolean useItem(ItemConvertible item, ItemUsageContext context) {
        ItemStack stack = context.getStack().copyComponentsToNewStack(item.asItem(), 1);
        return item.asItem().useOnBlock(createContextWithStack(context, stack)).isAccepted();
    }

    private static ItemUsageContext createContextWithStack(ItemUsageContext context, ItemStack stack) {
        BlockHitResult blockHitResult = new BlockHitResult(
                context.getHitPos(),
                context.getSide(),
                context.getBlockPos(),
                context.hitsInsideBlock()
        );

        return new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), stack, blockHitResult);
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return canMine(world, pos, stack) && super.canMine(stack, state, world, pos, user);
    }

    public record ToolDelegates(ItemConvertible shovel, ItemConvertible axe, ItemConvertible hoe) {
    }
}
