package net.cosmos.gui;

import net.cosmos.CosmosClient;
import net.cosmos.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private static long startTime = System.currentTimeMillis();

    public static void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        float elapsed = (System.currentTimeMillis() - startTime) / 1000f;
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // === ACTIVE MODULES (top-right) ===
        List<Module> active = new ArrayList<>();
        for (Module m : CosmosClient.moduleManager.getModules()) {
            if (m.isEnabled()) active.add(m);
        }

        int y = 2;
        for (Module m : active) {
            int hue = (int)(elapsed * 40 + active.indexOf(m) * 30) % 360;
            int color = hsvToRgb(hue, 0.9f, 1f);
            String text = m.name;
            int w = mc.textRenderer.getWidth(text);
            ctx.drawTextWithShadow(mc.textRenderer, text, screenW - w - 2, y, color);
            y += 10;
        }

        // === CLIENT TAG (top-left) ===
        String tag = "Cosmos v" + CosmosClient.VERSION;
        int tagColor = hsvToRgb((int)(elapsed * 60) % 360, 1f, 1f);
        ctx.drawTextWithShadow(mc.textRenderer, tag, 2, 2, tagColor);

        // === COORDS (bottom-left) ===
        if (mc.player != null) {
            var pos = mc.player.getBlockPos();
            String coords = String.format("XYZ: %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            ctx.drawTextWithShadow(mc.textRenderer, coords, 2, screenH - 22, 0xAAFFFFFF);
            String fps = "FPS: " + MinecraftClient.getCurrentFps();
            ctx.drawTextWithShadow(mc.textRenderer, fps, 2, screenH - 12, 0x88FFFFFF);
        }
    }

    private static int hsvToRgb(int h, float s, float v) {
        float f = (h % 360) / 60f, c = v * s, x = c * (1 - Math.abs(f % 2 - 1));
        float r, g, b;
        if (f < 1) { r=c; g=x; b=0; } else if (f < 2) { r=x; g=c; b=0; }
        else if (f < 3) { r=0; g=c; b=x; } else if (f < 4) { r=0; g=x; b=c; }
        else if (f < 5) { r=x; g=0; b=c; } else { r=c; g=0; b=x; }
        float m = v - c;
        return 0xFF000000 | ((int)((r+m)*255) << 16) | ((int)((g+m)*255) << 8) | (int)((b+m)*255);
    }
}
