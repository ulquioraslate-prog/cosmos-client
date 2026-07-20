package net.cosmos.module.modules.combat;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;

public class AntiKnockback extends Module {
    public final NumberSetting strength = addSetting(new NumberSetting("Strength", "Knockback reduction %", 100, 0, 100));
    public AntiKnockback() { super("AntiKnockback", "Reduce knockback received", Category.COMBAT); }
    // Applied via mixin in ClientPlayerEntityMixin
    public double getMultiplier() { return 1.0 - strength.getValue() / 100.0; }
}
