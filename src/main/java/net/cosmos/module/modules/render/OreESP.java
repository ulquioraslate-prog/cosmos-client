package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;

public class OreESP extends Module {
    public final BoolSetting diamonds  = addSetting(new BoolSetting("Diamonds",  "Show diamond ore",  true));
    public final BoolSetting gold      = addSetting(new BoolSetting("Gold",      "Show gold ore",      true));
    public final BoolSetting iron      = addSetting(new BoolSetting("Iron",      "Show iron ore",      true));
    public final BoolSetting ancient   = addSetting(new BoolSetting("Ancient",   "Show ancient debris",true));
    public final BoolSetting emerald   = addSetting(new BoolSetting("Emerald",   "Show emerald ore",   true));
    public OreESP() { super("OreESP", "Highlight ores through walls", Category.RENDER); }
    // Rendering done via WorldRendererMixin
}
