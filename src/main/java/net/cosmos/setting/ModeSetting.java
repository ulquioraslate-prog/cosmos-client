package net.cosmos.setting;

import java.util.Arrays;

public class ModeSetting extends Setting<String> {
    public final String[] modes;
    public ModeSetting(String name, String desc, String def, String... modes) {
        super(name, desc, def);
        this.modes = modes;
    }
    public void cycle() {
        int idx = Arrays.asList(modes).indexOf(value);
        value = modes[(idx + 1) % modes.length];
    }
    public boolean is(String m) { return value.equalsIgnoreCase(m); }
    @Override public String serialize() { return value; }
    @Override public void deserialize(String s) {
        for (String m : modes) if (m.equalsIgnoreCase(s)) { value = m; return; }
    }
}
