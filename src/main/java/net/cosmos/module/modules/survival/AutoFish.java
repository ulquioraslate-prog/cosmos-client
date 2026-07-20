package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Hand;

public class AutoFish extends Module {
    private boolean lineOut = false;
    private int timer = 0;

    public AutoFish() { super("AutoFish", "Automatic fishing", Category.SURVIVAL); }

    @Override public void onEnable() { lineOut = false; timer = 0; }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || !(c.player.getMainHandStack().getItem() instanceof FishingRodItem)) return;
        FishingBobberEntity b = c.player.fishHook;
        if (b == null && !lineOut) {
            c.interactionManager.interactItem(c.player, Hand.MAIN_HAND);
            lineOut = true; timer = 0;
        } else if (b != null && b.isSubmergedInWater() && b.getVelocity().y < -0.08) {
            c.interactionManager.interactItem(c.player, Hand.MAIN_HAND);
            lineOut = false; timer = 15;
        } else if (!lineOut) {
            if (timer-- <= 0) { c.interactionManager.interactItem(c.player, Hand.MAIN_HAND); lineOut = true; }
        }
    }
}
