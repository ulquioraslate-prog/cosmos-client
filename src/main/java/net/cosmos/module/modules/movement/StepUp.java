package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class StepUp extends Module {
    public final NumberSetting height = addSetting(new NumberSetting("Height", "Step-up height", 1.0, 0.5, 2.5));
    public StepUp() { super("StepUp", "Step up blocks automatically", Category.MOVEMENT); }
    @Override public void onEnable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player != null) c.player.setStepHeight(height.getValue().floatValue());
    }
    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player != null) c.player.setStepHeight(0.6f);
    }
    @Override public void onTick(MinecraftClient c) {
        if (c.player != null) c.player.setStepHeight(height.getValue().floatValue());
    }
}
