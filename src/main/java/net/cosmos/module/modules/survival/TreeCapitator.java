package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import java.util.*;

public class TreeCapitator extends Module {
    private static final Set<Block> LOGS = Set.of(
        Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
        Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG,
        Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD,
        Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.MANGROVE_WOOD,
        Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_BIRCH_LOG,
        Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_DARK_OAK_LOG
    );

    public final BoolSetting requireAxe = addSetting(new BoolSetting("RequireAxe", "Only with axe in hand", true));
    public final BoolSetting diagonal   = addSetting(new BoolSetting("Diagonal", "Include diagonal blocks", true));

    private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    private final HashSet<BlockPos> queued = new HashSet<>();

    public TreeCapitator() { super("TreeCapitator", "Chop entire tree at once", Category.SURVIVAL); }

    @Override public void onDisable() { queue.clear(); queued.clear(); }

    public void onLogBroken(MinecraftClient mc, BlockPos start) {
        if (!enabled || mc.world == null || mc.player == null) return;
        if (requireAxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof AxeItem)) return;

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> bfs = new ArrayDeque<>();
        bfs.add(start);
        visited.add(start);

        while (!bfs.isEmpty() && visited.size() < 192) {
            BlockPos p = bfs.poll();
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                if (!diagonal.getValue() && Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 1) continue;
                BlockPos n = p.add(dx, dy, dz);
                if (visited.contains(n)) continue;
                if (LOGS.contains(mc.world.getBlockState(n).getBlock())) {
                    visited.add(n);
                    bfs.add(n);
                    if (!queued.contains(n)) { queued.add(n); queue.add(n); }
                }
            }
        }
    }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.interactionManager == null) { queue.clear(); queued.clear(); return; }
        if (queue.isEmpty()) return;
        if (requireAxe.getValue() && !(c.player.getMainHandStack().getItem() instanceof AxeItem)) return;
        BlockPos p = queue.peek();
        if (c.world.getBlockState(p).isAir()) { queued.remove(queue.poll()); return; }
        if (c.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(p)) > 22.0) { queued.remove(queue.poll()); return; }
        c.interactionManager.updateBlockBreakingProgress(p, Direction.UP);
        c.player.swingHand(Hand.MAIN_HAND);
    }
}
