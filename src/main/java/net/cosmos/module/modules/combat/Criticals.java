package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;

public class Criticals extends Module {
    private boolean sent = false;

    public Criticals() { super("Criticals", "Always land critical hits", Category.COMBAT); }

    public void sendCritPackets(MinecraftClient c) {
        if (c.player == null || c.getNetworkHandler() == null) return;
        if (!c.player.isOnGround() || c.player.isTouchingWater() || c.player.hasVehicle()) return;
        double x = c.player.getX(), y = c.player.getY(), z = c.player.getZ();
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
    }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        boolean atk = c.options.attackKey.isPressed();
        if (atk && !sent && c.crosshairTarget instanceof EntityHitResult) {
            sendCritPackets(c);
            sent = true;
        }
        if (!atk) sent = false;
    }
}
