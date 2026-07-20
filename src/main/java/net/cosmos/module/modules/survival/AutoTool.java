package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoTool extends Module {
    public AutoTool() { super("AutoTool", "Auto-select best tool for block", Category.SURVIVAL); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        HitResult hit = c.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        BlockState bs = c.world.getBlockState(((BlockHitResult) hit).getBlockPos());
        var inv = c.player.getInventory();
        int best = -1;
        float bestSpeed = 1f;
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            float sp = s.getMiningSpeedMultiplier(bs);
            if (sp > bestSpeed) { bestSpeed = sp; best = i; }
        }
        if (best >= 0) inv.selectedSlot = best;
    }
}
