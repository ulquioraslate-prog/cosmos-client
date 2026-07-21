package net.cosmos.module.modules.building;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    public final BoolSetting tower    = addSetting(new BoolSetting("Tower", "Tower up when jumping", false));
    public final BoolSetting autoSwap = addSetting(new BoolSetting("AutoSwap", "Auto-select blocks from hotbar", true));

    public Scaffold() { super("Scaffold", "Place blocks under your feet", Category.BUILDING); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.interactionManager == null) return;

        if (!(c.player.getMainHandStack().getItem() instanceof BlockItem)) {
            if (!autoSwap.getValue()) return;
            var inv = c.player.getInventory();
            int found = -1;
            for (int i = 0; i < 9; i++) {
                var s = inv.getStack(i);
                if (!s.isEmpty() && s.getItem() instanceof BlockItem) { found = i; break; }
            }
            if (found < 0) return;
            inv.selectedSlot = found;
        }

        if (tower.getValue() && c.options.jumpKey.isPressed() && c.player.isOnGround()) {
            Vec3d v = c.player.getVelocity();
            c.player.setVelocity(v.x, 0.42, v.z);
        }

        BlockPos below = c.player.getBlockPos().down();
        if (!c.world.getBlockState(below).isAir()) return;

        for (Direction d : Direction.values()) {
            BlockPos n = below.offset(d);
            if (c.world.getBlockState(n).isAir()) continue;
            Vec3d hit = Vec3d.ofCenter(n).add(Vec3d.of(d.getOpposite().getVector()).multiply(0.5));
            c.interactionManager.interactBlock(c.player, Hand.MAIN_HAND,
                new BlockHitResult(hit, d.getOpposite(), n, false));
            c.player.swingHand(Hand.MAIN_HAND);
            return;
        }
    }
}
