package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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

    public VeinMiner() { super("VeinMiner", "Mine entire ore vein at once", Category.SURVIVAL); }

    public void onOreBroken(MinecraftClient mc, BlockPos start) {
        if (!enabled || mc.world == null || mc.player == null) return;
        if (requirePickaxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof PickaxeItem)) return;
        World w = mc.world;
        Block target = w.getBlockState(start).getBlock();
        if (!ORES.contains(target)) return;

        String tn = Registries.BLOCK.getId(target).getPath().replace("deepslate_", "");
        Set<BlockPos> visited = new LinkedHashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start); visited.add(start);
        int max = maxBlocks.getValue().intValue();

        while (!queue.isEmpty() && visited.size() < max) {
            BlockPos p = queue.poll();
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos n = p.add(dx, dy, dz);
                if (!visited.contains(n)) {
                    Block b = w.getBlockState(n).getBlock();
                    if (Registries.BLOCK.getId(b).getPath().replace("deepslate_", "").equals(tn)) {
                        visited.add(n); queue.add(n);
                    }
                }
            }
        }
        for (BlockPos p : visited) if (!p.equals(start)) {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, p, Direction.UP));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, p, Direction.UP));
        }
    }
}
