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
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Smooth speed", 0.2, 0.05, 1));

    private double prevFov = 70, curFov = 70;
    private boolean wasZooming = false;

    public Zoom() { super("Zoom", "Smooth zoom (hold Z)", Category.MOVEMENT); }

    @Override public void onTick(MinecraftClient c) {
        if (!enabled) return;
        boolean zooming = zoomKey.isPressed();
        if (zooming && !wasZooming) { prevFov = c.options.getFov().getValue(); }
        wasZooming = zooming;
        double target = zooming ? fov.getValue() : prevFov;
        curFov += (target - curFov) * speed.getValue();
        c.options.getFov().setValue((int) curFov);
    }
}
