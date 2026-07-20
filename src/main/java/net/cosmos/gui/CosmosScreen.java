package net.cosmos.gui;

import net.cosmos.CosmosClient;
import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.NumberSetting;
import net.cosmos.setting.ModeSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * CosmosScreen - The cosmic neon GUI.
 * Features: Animated starfield, nebula, RGB panels, module toggle, settings panel.
 * Open: Right Shift
 */
public class CosmosScreen extends Screen {

    // Panel dimensions
    private static final int PW  = 120; // panel width
    private static final int PH  = 210; // panel height
    private static final int CHH = 16;  // category header height
    private static final int MH  = 12;  // module row height
    private static final int GAP = 5;   // gap between panels

    // Settings panel
    private static final int SPW = 160; // settings panel width
    private static final int SPH = 200; // settings panel height

    private long openTime;
    private float animTick;
    private final Star[] stars = new Star[150];

    // Selected module for settings
    private Module selectedModule = null;
    private int settingsPanelX, settingsPanelY;

    private static final Category[] CATS = Category.values();

    public CosmosScreen() {
        super(Text.literal("Cosmos"));
        for (int i = 0; i < stars.length; i++) stars[i] = new Star();
    }

    @Override
    protected void init() {
        openTime = System.currentTimeMillis();
        for (Star s : stars) {
            s.x = (float)(Math.random() * width);
            s.y = (float)(Math.random() * height);
            s.size = (float)(Math.random() * 2.5 + 0.5);
            s.speed = (float)(Math.random() * 0.4 + 0.05);
            s.phase = (float)(Math.random() * Math.PI * 2);
        }
    }

    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        animTick += delta;
        float t = (System.currentTimeMillis() - openTime) / 1000f;

        // --- BACKGROUND ---
        ctx.fill(0, 0, width, height, 0xF203000F);

        // Nebula glow (animated pulsing radial gradient)
        int cx = width / 2, cy = height / 2;
        for (int r = 250; r > 0; r -= 6) {
            float ratio = r / 250f;
            int alpha  = (int)((1 - ratio) * 20);
            int hue    = ((int)(t * 15 + r * 0.3f)) % 360;
            ctx.fill(cx - r, cy - r * 2 / 3, cx + r, cy + r * 2 / 3, hsv(hue, 0.7f, 0.4f, alpha));
        }

        // Stars
        for (Star s : stars) {
            s.y += s.speed * delta;
            if (s.y > height) { s.y = 0; s.x = (float)(Math.random() * width); }
            float blink = (float)(0.4 + 0.6 * Math.sin(t * 1.5f + s.phase));
            int a = (int)(blink * 220 + 35);
            int sz = Math.max(1, (int)s.size);
            ctx.fill((int)s.x, (int)s.y, (int)s.x + sz, (int)s.y + sz, (a << 24) | 0xFFFFFF);
        }

        // --- HEADER ---
        String title = "COSMOS CLIENT v" + CosmosClient.VERSION;
        int titleColor = hsv((int)(t * 50) % 360, 1f, 1f, 255);
        ctx.drawCenteredTextWithShadow(textRenderer, title, cx, 8, titleColor);
        ctx.drawCenteredTextWithShadow(textRenderer, "Right Shift to close | Click module to toggle | Right-click for settings", cx, 20, 0x55AAAAAA);

        // Rainbow divider line
        for (int x = 0; x < width; x += 2) {
            int hue = ((int)(t * 50 + x * 360f / width)) % 360;
            ctx.fill(x, 30, x + 2, 31, hsv(hue, 1f, 1f, 200));
        }

        // --- PANELS ---
        int totalW = CATS.length * (PW + GAP) - GAP;
        int startX = (width - totalW) / 2;
        int startY = 38;

        for (int ci = 0; ci < CATS.length; ci++) {
            Category cat = CATS[ci];
            int px = startX + ci * (PW + GAP);

            // Slide-in animation
            float slideIn = Math.min(1f, t * 5 - ci * 0.12f);
            if (slideIn <= 0) continue;
            int pySl = (int)(startY - 25 * (1 - slideIn));
            int al = (int)(slideIn * 200);

            // Panel background (semi-transparent dark)
            ctx.fill(px, pySl, px + PW, pySl + PH, al << 24 | 0x04000C);

            // Animated RGB border
            int catHue = ((int)(t * 45 + ci * 60)) % 360;
            int borderColor = hsv(catHue, 0.9f, 1f, al);
            // Top/bot borders
            ctx.fill(px,        pySl,          px + PW, pySl + 1,     borderColor);
            ctx.fill(px,        pySl + PH - 1, px + PW, pySl + PH,    borderColor);
            // Side borders
            ctx.fill(px,        pySl,          px + 1,  pySl + PH,    borderColor);
            ctx.fill(px + PW-1, pySl,          px + PW, pySl + PH,    borderColor);

            // Category header
            int hdrColor = hsv(catHue, 0.5f, 0.5f, al);
            ctx.fill(px + 1, pySl + 1, px + PW - 1, pySl + CHH, hdrColor);
            ctx.drawCenteredTextWithShadow(textRenderer, cat.name,
                px + PW / 2, pySl + (CHH - 8) / 2 + 1, borderColor);

            // Modules
            List<Module> mods = CosmosClient.moduleManager.getByCategory(cat);
            int modY = pySl + CHH + 1;
            for (Module mod : mods) {
                if (modY + MH > pySl + PH - 1) break;
                boolean hov = mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH;

                // Row background
                if (mod.isEnabled()) {
                    float pulse = (float)(0.6 + 0.4 * Math.sin(t * 3 + ci + mods.indexOf(mod)));
                    ctx.fill(px + 1, modY, px + PW - 1, modY + MH,
                        hsv(catHue, 0.8f, pulse, (int)(al * 0.55)));
                    // Left indicator bar
                    ctx.fill(px + 1, modY, px + 3, modY + MH, borderColor);
                } else if (hov) {
                    ctx.fill(px + 1, modY, px + PW - 1, modY + MH, 0x22FFFFFF);
                }

                // Module name
                int tc = mod.isEnabled() ? 0xFFFFFFFF : (hov ? 0xFFCCCCCC : 0xFF777777);
                ctx.drawTextWithShadow(textRenderer, mod.name, px + 7, modY + (MH - 8) / 2 + 1, tc);

                // Settings icon (small dot if module has settings)
                if (!mod.getSettings().isEmpty()) {
                    ctx.fill(px + PW - 8, modY + MH/2 - 1, px + PW - 5, modY + MH/2 + 2, hov ? borderColor : 0xFF444444);
                }

                modY += MH;
            }
        }

        // --- SETTINGS PANEL ---
        if (selectedModule != null) {
            drawSettingsPanel(ctx, mx, my, t);
        }

        // Tooltips on hover
        for (int ci = 0; ci < CATS.length; ci++) {
            int px = startX + ci * (PW + GAP);
            List<Module> mods = CosmosClient.moduleManager.getByCategory(CATS[ci]);
            int modY = 38 + CHH + 1;
            for (Module mod : mods) {
                if (mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH)
                    drawTooltip(ctx, mod.description, mx, my);
                modY += MH;
                if (modY > 38 + PH - 1) break;
            }
        }

        super.render(ctx, mx, my, delta);
    }

    private void drawSettingsPanel(DrawContext ctx, int mx, int my, float t) {
        int px = settingsPanelX;
        int py = settingsPanelY;

        // Background
        ctx.fill(px, py, px + SPW, py + SPH, 0xEE050010);
        int hue = ((int)(t * 45)) % 360;
        int border = hsv(hue, 0.9f, 1f, 255);
        ctx.fill(px, py, px + SPW, py + 1, border);
        ctx.fill(px, py + SPH - 1, px + SPW, py + SPH, border);
        ctx.fill(px, py, px + 1, py + SPH, border);
        ctx.fill(px + SPW - 1, py, px + SPW, py + SPH, border);

        // Title
        ctx.drawCenteredTextWithShadow(textRenderer, selectedModule.name + " Settings",
            px + SPW / 2, py + 4, border);
        ctx.fill(px + 2, py + 14, px + SPW - 2, py + 15, 0x44FFFFFF);

        int sy = py + 18;
        for (var setting : selectedModule.getSettings()) {
            if (sy + 20 > py + SPH - 2) break;

            if (setting instanceof BoolSetting bs) {
                boolean hov = mx >= px + 2 && mx <= px + SPW - 2 && my >= sy && my <= sy + 14;
                ctx.drawTextWithShadow(textRenderer, bs.name, px + 6, sy + 3, 0xFFAAAAAA);
                int btc = bs.getValue() ? border : 0xFF444444;
                String val = bs.getValue() ? "ON" : "OFF";
                ctx.fill(px + SPW - 30, sy + 1, px + SPW - 2, sy + 13, bs.getValue() ? hsv(hue,0.8f,0.3f,200) : 0x22FFFFFF);
                ctx.drawCenteredTextWithShadow(textRenderer, val, px + SPW - 16, sy + 3, btc);
                sy += 16;

            } else if (setting instanceof NumberSetting ns) {
                ctx.drawTextWithShadow(textRenderer, ns.name, px + 6, sy + 2, 0xFFAAAAAA);
                String val = String.format("%.1f", ns.getValue());
                ctx.drawTextWithShadow(textRenderer, val, px + SPW - textRenderer.getWidth(val) - 4, sy + 2, border);
                // Progress bar
                int bw = SPW - 8;
                float pct = (float)((ns.getValue() - ns.min) / (ns.max - ns.min));
                ctx.fill(px + 4, sy + 12, px + 4 + bw, sy + 15, 0x33FFFFFF);
                ctx.fill(px + 4, sy + 12, px + 4 + (int)(bw * pct), sy + 15, border);
                sy += 20;

            } else if (setting instanceof ModeSetting ms) {
                ctx.drawTextWithShadow(textRenderer, ms.name, px + 6, sy + 3, 0xFFAAAAAA);
                ctx.drawTextWithShadow(textRenderer, ms.getValue(), px + SPW - textRenderer.getWidth(ms.getValue()) - 4, sy + 3, border);
                sy += 16;
            }
        }

        // Close hint
        ctx.drawCenteredTextWithShadow(textRenderer, "[Right-click to close]", px + SPW / 2, py + SPH - 10, 0x44888888);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int totalW = CATS.length * (PW + GAP) - GAP;
        int startX = (width - totalW) / 2;
        int startY = 38;

        // Close settings panel
        if (selectedModule != null && button == 1) {
            selectedModule = null;
            return true;
        }

        for (int ci = 0; ci < CATS.length; ci++) {
            Category cat = CATS[ci];
            int px = startX + ci * (PW + GAP);
            List<Module> mods = CosmosClient.moduleManager.getByCategory(cat);
            int modY = startY + CHH + 1;
            for (Module mod : mods) {
                if (mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH) {
                    if (button == 0) {
                        mod.toggle();
                        CosmosClient.configManager.save();
                    } else if (button == 1 && !mod.getSettings().isEmpty()) {
                        selectedModule = mod;
                        settingsPanelX = Math.min((int)mx + 5, width - SPW - 2);
                        settingsPanelY = Math.max(2, Math.min((int)my - 20, height - SPH - 2));
                    }
                    return true;
                }
                modY += MH;
                if (modY > startY + PH - 1) break;
            }
        }

        // Settings panel interaction
        if (selectedModule != null) {
            int px = settingsPanelX, py = settingsPanelY;
            int sy = py + 18;
            for (var setting : selectedModule.getSettings()) {
                if (setting instanceof BoolSetting bs) {
                    if (mx >= px + SPW - 30 && mx <= px + SPW - 2 && my >= sy + 1 && my <= sy + 13) {
                        bs.toggle();
                        CosmosClient.configManager.save();
                    }
                    sy += 16;
                } else if (setting instanceof NumberSetting ns) {
                    int bx = px + 4, bw = SPW - 8;
                    if (my >= sy + 10 && my <= sy + 17 && mx >= bx && mx <= bx + bw) {
                        double pct = (mx - bx) / bw;
                        ns.setValue(ns.min + pct * (ns.max - ns.min));
                        CosmosClient.configManager.save();
                    }
                    sy += 20;
                } else if (setting instanceof ModeSetting ms) {
                    if (mx >= px + 2 && mx <= px + SPW - 2 && my >= sy && my <= sy + 14) {
                        ms.cycle();
                        CosmosClient.configManager.save();
                    }
                    sy += 16;
                }
                if (sy > py + SPH - 2) break;
            }
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        // Drag sliders
        if (selectedModule != null && btn == 0) {
            int px = settingsPanelX, py = settingsPanelY;
            int sy = py + 18;
            for (var setting : selectedModule.getSettings()) {
                if (setting instanceof NumberSetting ns) {
                    int bx = px + 4, bw = SPW - 8;
                    if (my >= sy + 10 && my <= sy + 17) {
                        double pct = Math.max(0, Math.min(1, (mx - bx) / bw));
                        ns.setValue(ns.min + pct * (ns.max - ns.min));
                    }
                    sy += 20;
                } else {
                    sy += (setting instanceof BoolSetting) ? 16 : 16;
                }
                if (sy > py + SPH - 2) break;
            }
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    private void drawTooltip(DrawContext ctx, String text, int mx, int my) {
        int w = textRenderer.getWidth(text) + 8, h = 14;
        int tx = Math.min(mx + 8, width - w - 2);
        int ty = Math.max(my - h - 4, 2);
        ctx.fill(tx, ty, tx + w, ty + h, 0xDD010018);
        ctx.fill(tx, ty, tx + w, ty + 1, 0xFF00FFAA);
        ctx.drawTextWithShadow(textRenderer, text, tx + 4, ty + 3, 0xFF99FFDD);
    }

    private int hsv(int hue, float s, float v, int a) {
        float f = (hue % 360) / 60f, c = v * s, x = c * (1 - Math.abs(f % 2 - 1));
        float r, g, b;
        if (f<1){r=c;g=x;b=0;}else if(f<2){r=x;g=c;b=0;}else if(f<3){r=0;g=c;b=x;}
        else if(f<4){r=0;g=x;b=c;}else if(f<5){r=x;g=0;b=c;}else{r=c;g=0;b=x;}
        float m=v-c;
        return(a<<24)|((int)((r+m)*255)<<16)|((int)((g+m)*255)<<8)|(int)((b+m)*255);
    }

    private static class Star { float x, y, size, speed, phase; }
}
