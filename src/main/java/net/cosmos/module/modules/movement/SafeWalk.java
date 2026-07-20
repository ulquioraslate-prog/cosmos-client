package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;

public class SafeWalk extends Module {
    public SafeWalk() { super("SafeWalk", "Don't fall off edges", Category.MOVEMENT); }
    // Applied via mixin (sneak while at edge)
}
