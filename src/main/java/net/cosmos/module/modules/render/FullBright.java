package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {
    private double oldGamma = 1.0D;
    private boolean applied = false;

    public FullBright() {
        super("FullBright", "Максимальная яркость", Category.RENDER);
    }

    @Override
    public void onEnable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c == null || c.options == null) {
            applied = false;
            return;
        }
        oldGamma = c.options.getGamma().getValue();
        c.options.getGamma().setValue(16.0D);
        applied = true;
    }

    @Override
    public void onTick(MinecraftClient c) {
        if (c == null || c.options == null) return;
        if (!applied) {
            oldGamma = 1.0D;
            applied = true;
        }
        c.options.getGamma().setValue(16.0D);
    }

    @Override
    public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c != null && c.options != null && applied) {
            c.options.getGamma().setValue(oldGamma);
        }
        applied = false;
    }
}
