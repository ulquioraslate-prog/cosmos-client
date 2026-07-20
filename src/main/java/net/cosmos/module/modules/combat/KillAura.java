package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.ModeSetting;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    public final NumberSetting range   = addSetting(new NumberSetting("Range", "Attack range", 4.5, 1, 8));
    public final ModeSetting   target  = addSetting(new ModeSetting("Target", "Who to attack", "Hostile", "Hostile", "Players", "All"));
    public final BoolSetting   rotate  = addSetting(new BoolSetting("Rotate", "Rotate to target", true));
    public final NumberSetting cps     = addSetting(new NumberSetting("CPS", "Clicks per second", 12, 1, 20));

    private int attackTimer = 0;

    public KillAura() { super("KillAura", "Auto attack nearby entities", Category.COMBAT); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        if (c.currentScreen != null) return;

        int delay = (int)(20.0 / cps.getValue());
        if (attackTimer > 0) { attackTimer--; return; }

        double rangeSq = range.getValue() * range.getValue();
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity e : c.world.getEntities()) {
            if (!(e instanceof LivingEntity le)) continue;
            if (le == c.player) continue;
            if (le.isDead() || !le.isAlive()) continue;
            if (le.distanceTo(c.player) > range.getValue()) continue;

            boolean valid = switch (target.getValue()) {
                case "Hostile"  -> le instanceof Monster || le instanceof HostileEntity;
                case "Players" -> le instanceof PlayerEntity;
                case "All"     -> !(le instanceof net.minecraft.entity.passive.PassiveEntity);
                default        -> false;
            };
            if (valid) targets.add(le);
        }

        if (targets.isEmpty()) return;

        // Closest target
        targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(c.player)));
        LivingEntity best = targets.get(0);

        if (rotate.getValue()) {
            double dx = best.getX() - c.player.getX();
            double dy = best.getEyeY() - c.player.getEyeY();
            double dz = best.getZ() - c.player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            float yaw   = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
            c.player.setYaw(yaw);
            c.player.setPitch(pitch);
        }

        c.interactionManager.attackEntity(c.player, best);
        c.player.swingHand(Hand.MAIN_HAND);
        attackTimer = delay;
    }
}
