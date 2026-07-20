package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import java.util.*;

public class AutoRestock extends Module {
    private final Map<Integer, Item> snapshot = new HashMap<>();
    public final NumberSetting threshold = addSetting(new NumberSetting("Threshold", "Restock when count below", 5, 1, 64));

    public AutoRestock() { super("AutoRestock", "Auto-refill hotbar slots with same items", Category.SURVIVAL); }

    @Override public void onEnable() { snapshot.clear(); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        var inv = c.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack cur = inv.getStack(i);
            if (cur.isEmpty() || cur.getCount() <= threshold.getValue().intValue()) {
                Item need = snapshot.get(i);
                if (need != null) restoreSlot(c, i, need);
            } else {
                snapshot.put(i, cur.getItem());
            }
        }
    }

    private void restoreSlot(MinecraftClient c, int slot, Item target) {
        var inv = c.player.getInventory();
        for (int i = 9; i < 36; i++) {
            var s = inv.getStack(i);
            if (!s.isEmpty() && s.getItem() == target) {
                c.interactionManager.clickSlot(c.player.playerScreenHandler.syncId, i, slot, SlotActionType.SWAP, c.player);
                return;
            }
        }
        snapshot.remove(slot);
    }
}
