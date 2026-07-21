package net.cosmos.module.modules.building;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.SlotActionType;

public class AutoCraft extends Module {
    public final ModeSetting item = addSetting(new ModeSetting("Item", "What to craft", "Planks",
        "Planks", "Sticks", "Torch", "CraftingTable", "Bread"));
    private int delay = 0;

    public AutoCraft() { super("AutoCraft", "Auto-craft item (open inventory or crafting table)", Category.BUILDING); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.interactionManager == null) return;
        if (!(c.currentScreen instanceof InventoryScreen) && !(c.currentScreen instanceof CraftingScreen)) return;
        if (delay > 0) { delay--; return; }

        Item want;
        if (item.is("Sticks")) want = Items.STICK;
        else if (item.is("Torch")) want = Items.TORCH;
        else if (item.is("CraftingTable")) want = Items.CRAFTING_TABLE;
        else if (item.is("Bread")) want = Items.BREAD;
        else want = Items.OAK_PLANKS;

        for (Recipe<?> r : c.world.getRecipeManager().values()) {
            if (r.getType() != RecipeType.CRAFTING) continue;
            if (r.getOutput(c.world.getRegistryManager()).getItem() != want) continue;
            var handler = c.player.currentScreenHandler;
            c.interactionManager.clickRecipe(handler.syncId, r, true);
            c.interactionManager.clickSlot(handler.syncId, 0, 0, SlotActionType.QUICK_MOVE, c.player);
            delay = 4;
            return;
        }
    }
}
