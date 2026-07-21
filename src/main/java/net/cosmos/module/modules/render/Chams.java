package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

public class Chams extends Module {
    public final BoolSetting players = addSetting(new BoolSetting("Players", "Players visible through walls", true));
    public final BoolSetting mobs    = addSetting(new BoolSetting("Mobs",    "Mobs visible through walls",    false));

    public Chams() { super("Chams", "See entities through walls (glow)", Category.RENDER); }

    @Override public void onTick(MinecraftClient c) {
        if (c.world == null || c.player == null) return;
        for (Entity e : c.world.getEntities()) {
            if (e == c.player) continue;
            boolean want = (players.getValue() && e instanceof PlayerEntity)
                        || (mobs.getValue() && e instanceof Monster);
            if (want && !e.isGlowing()) e.setGlowing(true);
            if (!want && e.isGlowing()) e.setGlowing(false);
        }
    }

    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.world != null) for (Entity e : c.world.getEntities()) e.setGlowing(false);
    }
}
