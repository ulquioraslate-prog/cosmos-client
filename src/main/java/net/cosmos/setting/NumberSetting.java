package net.cosmos.setting;

public class NumberSetting extends Setting<Double> {
    public final double min, max;
    public NumberSetting(String name, String desc, double def, double min, double max) {
        super(name, desc, def);
        this.min = min;
        this.max = max;
    }
    public void increment(double step) { setValue(Math.max(min, Math.min(max, value + step))); }
    public void decrement(double step) { setValue(Math.max(min, Math.min(max, value - step))); }
    @Override public String serialize() { return value.toString(); }
    @Override public void deserialize(String s) {
        try { setValue(Double.parseDouble(s)); } catch (NumberFormatException ignored) {}
    }
}
