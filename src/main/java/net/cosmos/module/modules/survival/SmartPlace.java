package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class SmartPlace extends Module {
    private Item lastBlock = null;
    private int delay = 0;

    public final BoolSetting smartSwap   = addSetting(new BoolSetting("SmartSwap", "Find same block in inventory", true));
    public final BoolSetting fastPlace   = addSetting(new BoolSetting("FastPlace", "No delay between placements", true));

    public SmartPlace() { super("SmartPlace", "Smart block placement with auto-restock", Category.SURVIVAL); }

    @Override
    public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        if (!c.options.useKey.isPressed()) { delay = 0; return; }
        if (delay > 0) { delay--; return; }

        ItemStack held = c.player.getMainHandStack();
        if ((held.isEmpty() || !(held.getItem() instanceof BlockItem)) && smartSwap.getValue()) {
            if (lastBlock != null) restoreBlock(c, lastBlock);
            held = c.player.getMainHandStack();
        } else if (!held.isEmpty() && held.getItem() instanceof BlockItem) {
            lastBlock = held.getItem();
        }
        // Preload when stack is at 1
        if (smartSwap.getValue() && !held.isEmpty() && held.getCount() == 1 && lastBlock != null)
            preload(c, lastBlock);
        if (held.isEmpty()) return;

        HitResult hit = c.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            c.interactionManager.interactBlock(c.player, Hand.MAIN_HAND, (BlockHitResult) hit);
            delay = fastPlace.getValue() ? 0 : 2;
        }
    }

    private void restoreBlock(MinecraftClient c, Item t) {
        var inv = c.player.getInventory();
        for (int i = 0; i < 9; i++) { var s = inv.getStack(i); if (!s.isEmpty() && s.getItem() == t) { inv.selectedSlot = i; return; } }
        for (int i = 9; i < 36; i++) { var s = inv.getStack(i); if (!s.isEmpty() && s.getItem() == t) {
            c.interactionManager.clickSlot(c.player.playerScreenHandler.syncId, i, 8, SlotActionType.SWAP, c.player);
            inv.selectedSlot = 8; return;
        }}
    }

    private void preload(MinecraftClient c, Item t) {
        var inv = c.player.getInventory();
        for (int i = 9; i < 36; i++) { var s = inv.getStack(i); if (!s.isEmpty() && s.getItem() == t) {
            c.interactionManager.clickSlot(c.player.playerScreenHandler.syncId, i, inv.selectedSlot, SlotActionType.SWAP, c.player);
            break;
        }}
    }
}
