package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {
    private double prev = 1.0;
    public final NumberSetting gamma = addSetting(new NumberSetting("Gamma", "Brightness level", 100, 1, 200));
    public FullBright() { super("FullBright", "Maximum brightness, see in the dark", Category.RENDER); }
    @Override public void onEnable() { MinecraftClient c = MinecraftClient.getInstance(); prev = c.options.getGamma().getValue(); c.options.getGamma().setValue(gamma.getValue()); }
    @Override public void onDisable() { MinecraftClient.getInstance().options.getGamma().setValue(prev); }
    @Override public void onTick(MinecraftClient c) { c.options.getGamma().setValue(gamma.getValue()); }
}
