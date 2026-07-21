package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
    private static final KeyBinding zoomKey;
    static { zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.cosmosclient.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.cosmosclient")); }

    public final NumberSetting fov   = addSetting(new NumberSetting("FOV",   "Zoomed FOV",    10, 1, 40));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Smooth speed", 0.3, 0.05, 1));

    private double prevFov = 70, curFov = 70;
    private boolean captured = false;

    public Zoom() { super("Zoom", "Smooth zoom (hold Z)", Category.MOVEMENT); }

    @Override public void onEnable() { captured = false; }

    @Override public void onTick(MinecraftClient c) {
        if (c.options == null) return;
        if (!captured) {
            prevFov = c.options.getFov().getValue();
            curFov = prevFov;
            captured = true;
        }
        boolean zooming = zoomKey.isPressed();
        double target = zooming ? fov.getValue() : prevFov;
        curFov += (target - curFov) * speed.getValue();
        c.options.getFov().setValue((int) Math.round(curFov));
    }

    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (captured && c != null && c.options != null) c.options.getFov().setValue((int) prevFov);
        captured = false;
    }
}
