package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {
    public Criticals() { super("Criticals", "Always land critical hits", Category.COMBAT); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || !c.player.isOnGround()) return;
        // Micro jump for critical
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            c.player.getX(), c.player.getY() + 0.0625, c.player.getZ(), false));
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            c.player.getX(), c.player.getY(), c.player.getZ(), false));
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            c.player.getX(), c.player.getY() + 1.1e-5, c.player.getZ(), false));
        c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
