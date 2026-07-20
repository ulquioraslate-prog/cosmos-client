package net.cosmos;

import net.cosmos.config.ConfigManager;
import net.cosmos.event.EventBus;
import net.cosmos.gui.CosmosScreen;
import net.cosmos.gui.HudRenderer;
import net.cosmos.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmosClient implements ClientModInitializer {

    public static final String NAME    = "Cosmos";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER  = LoggerFactory.getLogger(NAME);

    public static ModuleManager moduleManager;
    public static ConfigManager  configManager;
    public static EventBus       eventBus;
    public static CosmosScreen   cosmosScreen;

    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[{}] Starting v{}", NAME, VERSION);

        eventBus     = new EventBus();
        moduleManager = new ModuleManager();
        configManager = new ConfigManager();
        cosmosScreen  = new CosmosScreen();

        // Load saved config
        configManager.load();

        // Register Right Shift key
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cosmosclient.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.cosmosclient"
        ));

        // Tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiKey.wasPressed()) {
                if (client.currentScreen instanceof CosmosScreen) client.setScreen(null);
                else client.setScreen(cosmosScreen);
            }
            if (client.player != null) {
                moduleManager.onTick(client);
            }
        });

        // HUD
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            if (MinecraftClient.getInstance().player != null) {
                HudRenderer.render(ctx, tickDelta);
            }
        });

        LOGGER.info("[{}] Ready! Modules: {}", NAME, moduleManager.getModules().size());
    }
}
