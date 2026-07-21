package net.cosmos.module.modules.utility;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;

public class FPSBoost extends Module {
    public final BoolSetting clouds    = addSetting(new BoolSetting("NoClouds",    "Disable clouds",   true));
    public final BoolSetting particles = addSetting(new BoolSetting("NoParticles", "Reduce particles", true));

    public FPSBoost() { super("FPSBoost", "Improve performance", Category.UTILITY); }

    @Override public void onTick(MinecraftClient c) {
        if (clouds.getValue()) c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        if (particles.getValue()) c.options.getParticles().setValue(ParticlesMode.MINIMAL);
    }
}
