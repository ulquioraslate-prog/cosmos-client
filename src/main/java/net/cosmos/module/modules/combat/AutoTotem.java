package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    public final NumberSetting hpThreshold = addSetting(new NumberSetting("HP", "HP to trigger", 8, 1, 20));
    public final BoolSetting   always      = addSetting(new BoolSetting("Always", "Keep totem always, not only low HP", true));

    public AutoTotem() { super("AutoTotem", "Auto hold totem in offhand", Category.COMBAT); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.interactionManager == null) return;
        if (c.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
        if (!always.getValue() && c.player.getHealth() > hpThreshold.getValue()) return;

        var inv = c.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                int slotId = i < 9 ? 36 + i : i;
                c.interactionManager.clickSlot(
                    c.player.playerScreenHandler.syncId,
                    slotId, 40, SlotActionType.SWAP, c.player);
                return;
            }
        }
    }
}
