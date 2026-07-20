package net.cosmos.module.modules.movement;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Speed extends Module {
    public final NumberSetting multiplier = addSetting(new NumberSetting("Multiplier", "Speed multiplier", 2.0, 1.0, 5.0));
    public Speed() { super("Speed", "Move faster", Category.MOVEMENT); }
    @Override public void onEnable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player != null)
            c.player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 9999999, (int)(multiplier.getValue() - 1), false, false, false));
    }
    @Override public void onDisable() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player != null) c.player.removeStatusEffect(StatusEffects.SPEED);
    }
    @Override public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        if (!c.player.hasStatusEffect(StatusEffects.SPEED))
            c.player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 9999999, (int)(multiplier.getValue() - 1), false, false, false));
    }
}
