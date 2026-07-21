package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    public final NumberSetting multiplier = addSetting(new NumberSetting("Multiplier", "Speed multiplier", 1.6, 1.0, 3.0));

    public Speed() { super("Speed", "Move faster", Category.MOVEMENT); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.currentScreen != null) return;
        if (!c.options.forwardKey.isPressed()) return;
        if (!c.player.isOnGround()) return;
        Vec3d v = c.player.getVelocity();
        Vec3d dir = Vec3d.fromPolar(0, c.player.getYaw()).normalize();
        double sp = 0.22 * multiplier.getValue();
        c.player.setVelocity(dir.x * sp, v.y, dir.z * sp);
    }
}
