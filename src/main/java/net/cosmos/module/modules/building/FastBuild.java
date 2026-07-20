package net.cosmos.module.modules.building;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class FastBuild extends Module {
    public FastBuild() { super("FastBuild", "Instant block placement, no delay", Category.BUILDING); }
    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || !c.options.useKey.isPressed()) return;
        HitResult h = c.crosshairTarget;
        if (h != null && h.getType() == HitResult.Type.BLOCK)
            c.interactionManager.interactBlock(c.player, Hand.MAIN_HAND, (BlockHitResult) h);
    }
}
