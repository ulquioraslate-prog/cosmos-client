package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;

public class AutoSprint extends Module {
    public AutoSprint() { super("AutoSprint", "Always sprint", Category.MOVEMENT); }
    @Override public void onTick(MinecraftClient c) {
        if (c.player != null && c.options.forwardKey.isPressed()) c.player.setSprinting(true);
    }
}
