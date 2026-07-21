package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class AutoEat extends Module {
    public final NumberSetting hunger = addSetting(new NumberSetting("HungerLevel", "Eat when hunger below", 17, 1, 20));

    private boolean eating = false;
    private int prevSlot = -1;

    public AutoEat() { super("AutoEat", "Auto-eat best food when hungry", Category.SURVIVAL); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) { stopEating(c); return; }
        var hm = c.player.getHungerManager();
        var inv = c.player.getInventory();

        if (eating) {
            ItemStack held = inv.getStack(inv.selectedSlot);
            if (hm.getFoodLevel() >= 20 || held.isEmpty() || !held.getItem().isFood()) {
                stopEating(c);
                return;
            }
            c.options.useKey.setPressed(true);
            return;
        }

        if (hm.getFoodLevel() >= hunger.getValue().intValue()) return;
        if (c.currentScreen != null) return;

        int best = -1, bestN = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.getItem().isFood()) {
                int n = s.getItem().getFoodComponent().getHunger();
                if (n > bestN) { bestN = n; best = i; }
            }
        }
        if (best >= 0) {
            prevSlot = inv.selectedSlot;
            inv.selectedSlot = best;
            eating = true;
            c.options.useKey.setPressed(true);
        }
    }

    private void stopEating(MinecraftClient c) {
        if (!eating) return;
        eating = false;
        c.options.useKey.setPressed(false);
        if (c.player != null && prevSlot >= 0) c.player.getInventory().selectedSlot = prevSlot;
        prevSlot = -1;
    }

    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c != null) stopEating(c);
    }
}
