package net.cosmos.config;

import net.cosmos.CosmosClient;
import net.cosmos.module.Module;
import net.cosmos.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigManager {
    private final Path configFile;

    public ConfigManager() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("cosmos-client.properties");
    }

    public void save() {
        Properties props = new Properties();
        for (Module m : CosmosClient.moduleManager.getModules()) {
            props.setProperty(m.name + ".enabled", String.valueOf(m.isEnabled()));
            for (Setting<?> s : m.getSettings()) {
                props.setProperty(m.name + "." + s.name, s.serialize());
            }
        }
        try (OutputStream out = Files.newOutputStream(configFile)) {
            props.store(out, "Cosmos Client Config");
        } catch (IOException e) {
            CosmosClient.LOGGER.error("[Cosmos] Failed to save config", e);
        }
    }

    public void load() {
        if (!Files.exists(configFile)) return;
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            CosmosClient.LOGGER.error("[Cosmos] Failed to load config", e);
            return;
        }
        for (Module m : CosmosClient.moduleManager.getModules()) {
            String en = props.getProperty(m.name + ".enabled");
            if (en != null) m.setEnabled(Boolean.parseBoolean(en));
            for (Setting<?> s : m.getSettings()) {
                String val = props.getProperty(m.name + "." + s.name);
                if (val != null) s.deserialize(val);
            }
        }
    }
}
