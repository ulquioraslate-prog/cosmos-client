package net.cosmos.module.modules.utility;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClipboardCoords extends Module {
    private static final KeyBinding key = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("key.cosmosclient.coords", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_DECIMAL, "category.cosmosclient"));
    public ClipboardCoords() { super("ClipboardCoords", "Copy coords to clipboard (Numpad .)", Category.UTILITY); }
    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        while (key.wasPressed()) {
            var p = c.player.getBlockPos();
            String coords = p.getX() + " " + p.getY() + " " + p.getZ();
            c.keyboard.setClipboard(coords);
            c.player.sendMessage(Text.literal("§aCopied: " + coords), true);
        }
    }
}
