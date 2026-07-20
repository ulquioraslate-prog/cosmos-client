package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
    public final NumberSetting hunger = addSetting(new NumberSetting("HungerLevel", "Eat when hunger below", 17, 1, 20));

    public AutoEat() { super("AutoEat", "Auto-eat best food when hungry", Category.SURVIVAL); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        if (c.player.getHungerManager().getFoodLevel() >= hunger.getValue().intValue()) return;
        var inv = c.player.getInventory();
        int best = -1, bestN = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.getItem().isFood()) {
                int n = s.getItem().getFoodComponent().getHunger();
                if (n > bestN) { bestN = n; best = i; }
            }
        }
        if (best >= 0) {
            int prev = inv.selectedSlot;
            inv.selectedSlot = best;
            c.interactionManager.interactItem(c.player, Hand.MAIN_HAND);
            inv.selectedSlot = prev;
        }
    }
}
