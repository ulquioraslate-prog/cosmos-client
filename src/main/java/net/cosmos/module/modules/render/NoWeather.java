package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;

public class NoWeather extends Module {
    public NoWeather() { super("NoWeather", "Remove rain and thunder visually", Category.RENDER); }
    @Override public void onTick(MinecraftClient c) {
        if (c.world != null) { c.world.setRainGradient(0); c.world.setThunderGradient(0); }
    }
}
