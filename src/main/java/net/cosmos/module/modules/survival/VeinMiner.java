package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import java.util.*;

public class VeinMiner extends Module {
    private static final Set<Block> ORES = Set.of(
        Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
        Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
        Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
        Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
        Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS
    );

    public final BoolSetting requirePickaxe = addSetting(new BoolSetting("RequirePickaxe", "Only with pickaxe", true));
    public final NumberSetting maxBlocks     = addSetting(new NumberSetting("MaxBlocks", "Max blocks to mine", 64, 1, 256));

    private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    private final HashSet<BlockPos> queued = new HashSet<>();

    public VeinMiner() { super("VeinMiner", "Mine entire ore vein at once", Category.SURVIVAL); }

    @Override public void onDisable() { queue.clear(); queued.clear(); }

    private static String normalize(Block b) {
        return Registries.BLOCK.getId(b).getPath().replace("deepslate_", "");
    }

    public void onOreBroken(MinecraftClient mc, BlockPos start) {
        if (!enabled || mc.world == null || mc.player == null) return;
        if (requirePickaxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof PickaxeItem)) return;

        String tn = null;
        for (Direction d : Direction.values()) {
            Block b = mc.world.getBlockState(start.offset(d)).getBlock();
            if (ORES.contains(b)) { tn = normalize(b); break; }
        }
        if (tn == null) return;

        int max = maxBlocks.getValue().intValue();
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> bfs = new ArrayDeque<>();
        bfs.add(start);
        visited.add(start);

        while (!bfs.isEmpty() && visited.size() < max) {
            BlockPos p = bfs.poll();
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos n = p.add(dx, dy, dz);
                if (visited.contains(n)) continue;
                Block b = mc.world.getBlockState(n).getBlock();
                if (ORES.contains(b) && normalize(b).equals(tn)) {
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
        if (requirePickaxe.getValue() && !(c.player.getMainHandStack().getItem() instanceof PickaxeItem)) return;
        BlockPos p = queue.peek();
        if (c.world.getBlockState(p).isAir()) { queued.remove(queue.poll()); return; }
        if (c.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(p)) > 22.0) { queued.remove(queue.poll()); return; }
        c.interactionManager.updateBlockBreakingProgress(p, Direction.UP);
        c.player.swingHand(Hand.MAIN_HAND);
    }
}
