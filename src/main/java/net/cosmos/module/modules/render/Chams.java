package net.cosmos.module.modules.render;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;

public class Chams extends Module {
    public final BoolSetting players = addSetting(new BoolSetting("Players", "Players visible through walls", true));
    public final BoolSetting mobs    = addSetting(new BoolSetting("Mobs",    "Mobs visible through walls",    false));
    public Chams() { super("Chams", "See entities through walls", Category.RENDER); }
    // Applied via mixin
}
