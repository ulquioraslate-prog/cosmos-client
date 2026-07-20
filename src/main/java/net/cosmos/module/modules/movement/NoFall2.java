package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall2 extends Module {
    public NoFall2() { super("NoFall", "Prevent fall damage (movement)", Category.MOVEMENT); }
    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        if (c.player.fallDistance > 2f)
            c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
