package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.util.ISimpleOption;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {
    private double oldGamma = 1.0;
    private boolean applied = false;

    public FullBright() { super("FullBright", "Максимальная яркость", Category.RENDER); }

    @SuppressWarnings("unchecked")
    private static void forceGamma(MinecraftClient c, double v) {
        if (c == null || c.options == null) return;
        Object opt = c.options.getGamma();
        ((ISimpleOption<Double>) opt).cosmos$forceSetValue(v);
    }

    @Override public void onEnable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c == null || c.options == null) return;
        oldGamma = c.options.getGamma().getValue();
        forceGamma(c, 16.0);
        applied = true;
    }

    @Override public void onTick(MinecraftClient c) {
        if (!applied && c.options != null) {
            oldGamma = c.options.getGamma().getValue();
            applied = true;
        }
        forceGamma(c, 16.0);
    }

    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (applied) forceGamma(c, Math.min(oldGamma, 1.0));
        applied = false;
    }
}
