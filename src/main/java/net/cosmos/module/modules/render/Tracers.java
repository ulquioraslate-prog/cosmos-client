package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.ModeSetting;

public class Tracers extends Module {
    public final BoolSetting players = addSetting(new BoolSetting("Players", "Trace players", true));
    public final BoolSetting mobs    = addSetting(new BoolSetting("Mobs",    "Trace hostile mobs", true));
    public final BoolSetting items   = addSetting(new BoolSetting("Items",   "Trace dropped items", false));
    public final ModeSetting mode    = addSetting(new ModeSetting("Mode",    "Tracer mode", "Eyes", "Eyes", "Middle", "Feet"));
    public Tracers() { super("Tracers", "Draw lines to entities", Category.RENDER); }
    // Rendering via WorldRendererMixin
}
