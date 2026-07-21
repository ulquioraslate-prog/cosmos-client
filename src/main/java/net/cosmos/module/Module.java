package net.cosmos.module;

import net.cosmos.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    protected boolean enabled = false;
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() { return settings; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean e) {
        this.enabled = e;
        if (e) try { onEnable(); } catch (Throwable cosmosErr) { System.out.println("[Cosmos] enable error: " + cosmosErr); } else try { onDisable(); } catch (Throwable cosmosErr) { System.out.println("[Cosmos] disable error: " + cosmosErr); }
    }

    public void toggle() { setEnabled(!enabled); }

    public void onEnable()  {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}
    public void onRender(net.minecraft.client.util.math.MatrixStack matrices, float delta) {}
}
