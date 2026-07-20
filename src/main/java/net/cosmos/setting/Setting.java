package net.cosmos.setting;

public abstract class Setting<T> {
    public final String name;
    public final String description;
    protected T value;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public T getValue() { return value; }
    public void setValue(T v) { value = v; }
    public abstract String serialize();
    public abstract void deserialize(String s);
}
