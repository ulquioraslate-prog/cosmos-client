package net.cosmos.module.modules.utility;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.NumberSetting;

public class Timer extends Module {
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Timer multiplier", 2.0, 0.1, 10.0));
    public Timer() { super("Timer", "Control game timer speed", Category.UTILITY); }
    // Applied via mixin
    public float getMultiplier() { return enabled ? speed.getValue().floatValue() : 1f; }
}
