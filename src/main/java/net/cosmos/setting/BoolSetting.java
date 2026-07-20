package net.cosmos.setting;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String name, String desc, boolean def) {
        super(name, desc, def);
    }
    public void toggle() { value = !value; }
    @Override public String serialize() { return value.toString(); }
    @Override public void deserialize(String s) { value = Boolean.parseBoolean(s); }
}
