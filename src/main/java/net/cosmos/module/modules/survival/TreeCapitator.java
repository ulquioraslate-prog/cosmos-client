package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.AxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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

    public TreeCapitator() { super("TreeCapitator", "Chop entire tree at once", Category.SURVIVAL); }

    public void onLogBroken(MinecraftClient mc, BlockPos start) {
        if (!enabled || mc.world == null || mc.player == null) return;
        if (requireAxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof AxeItem)) return;
        World world = mc.world;
        Block target = world.getBlockState(start).getBlock();
        if (!LOGS.contains(target)) return;

        Set<BlockPos> visited = new LinkedHashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start); visited.add(start);

        while (!queue.isEmpty() && visited.size() < 128) {
            BlockPos p = queue.poll();
            int range = diagonal.getValue() ? 1 : 0;
            for (int dx = -1; dx <= 1; dx++) for (int dy = 0; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                if (!diagonal.getValue() && Math.abs(dx) + Math.abs(dz) > 1) continue;
                BlockPos n = p.add(dx, dy, dz);
                if (!visited.contains(n) && world.getBlockState(n).getBlock() == target) {
                    visited.add(n); queue.add(n);
                }
            }
        }
        for (BlockPos p : visited) {
            if (!p.equals(start)) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, p, Direction.UP));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, p, Direction.UP));
            }
        }
    }
}
