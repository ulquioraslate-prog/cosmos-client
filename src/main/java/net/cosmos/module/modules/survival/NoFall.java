package net.cosmos.module.modules.survival;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public NoFall() { super("NoFall", "Prevent fall damage", Category.SURVIVAL); }

    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        if (c.player.fallDistance > 2.5f)
            c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
