package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    public final NumberSetting hpThreshold = addSetting(new NumberSetting("HP", "HP to trigger", 8, 1, 20));

    public AutoTotem() { super("AutoTotem", "Auto hold totem when low HP", Category.COMBAT); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        float hp = c.player.getHealth();
        if (hp > hpThreshold.getValue()) return;

        // Check offhand already has totem
        if (c.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        // Find totem in inventory
        var inv = c.player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                // Move to offhand slot (slot 45)
                c.interactionManager.clickSlot(
                    c.player.playerScreenHandler.syncId,
                    c.player.playerScreenHandler.getSlotIndex(inv, i).orElse(-1),
                    40, SlotActionType.SWAP, c.player
                );
                return;
            }
        }
    }
}
