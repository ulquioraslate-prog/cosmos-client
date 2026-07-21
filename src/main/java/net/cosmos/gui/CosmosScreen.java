package net.cosmos.gui;

import net.cosmos.CosmosClient;
import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.cosmos.setting.ModeSetting;
import net.cosmos.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class CosmosScreen extends Screen {

    private static final int PW = 120, PH = 210, CHH = 16, MH = 12, GAP = 5;
    private static final int SPW = 180, SPH = 200;

    private long openTime;
    private final Star[] stars = new Star[150];

    private Module selectedModule = null;
    private float scrollX = 0;
    private boolean draggingScroll = false;

    private static final Category[] CATS = Category.values();

    public CosmosScreen() {
        super(Text.literal("Cosmos"));
        for (int i = 0; i < stars.length; i++) stars[i] = new Star();
    }

    @Override
    protected void init() {
        openTime = System.currentTimeMillis();
        for (Star s : stars) {
            s.x = (float) (Math.random() * width);
            s.y = (float) (Math.random() * height);
            s.size = (float) (Math.random() * 2.5 + 0.5);
            s.speed = (float) (Math.random() * 0.4 + 0.05);
            s.phase = (float) (Math.random() * Math.PI * 2);
        }
    }

    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }

    private int totalPanelsWidth() { return CATS.length * (PW + GAP) - GAP; }

    private int baseX() {
        int totalW = totalPanelsWidth();
        if (totalW <= width - 16) return (width - totalW) / 2;
        return 8 - (int) scrollX;
    }

    private float maxScroll() { return Math.max(0, totalPanelsWidth() - (width - 16)); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        float t = (System.currentTimeMillis() - openTime) / 1000f;
        int cx = width / 2;

        ctx.fill(0, 0, width, height, 0xF203000F);
        for (int r = 250; r > 0; r -= 6) {
            float ratio = r / 250f;
            int alpha = (int) ((1 - ratio) * 20);
            int hue = ((int) (t * 15 + r * 0.3f)) % 360;
            ctx.fill(cx - r, height / 2 - r * 2 / 3, cx + r, height / 2 + r * 2 / 3, hsv(hue, 0.7f, 0.4f, alpha));
        }
        for (Star s : stars) {
            s.y += s.speed * delta;
            if (s.y > height) { s.y = 0; s.x = (float) (Math.random() * width); }
            float blink = (float) (0.4 + 0.6 * Math.sin(t * 1.5f + s.phase));
            int a = (int) (blink * 220 + 35);
            int sz = Math.max(1, (int) s.size);
            ctx.fill((int) s.x, (int) s.y, (int) s.x + sz, (int) s.y + sz, (a << 24) | 0xFFFFFF);
        }

        ctx.drawCenteredTextWithShadow(textRenderer, "COSMOS CLIENT v" + CosmosClient.VERSION, cx, 8,
            hsv((int) (t * 50) % 360, 1f, 1f, 255));
        ctx.drawCenteredTextWithShadow(textRenderer, "LMB toggle | RMB settings | drag bottom bar to scroll", cx, 20, 0x55AAAAAA);
        for (int x = 0; x < width; x += 2) {
            int hue = ((int) (t * 50 + x * 360f / width)) % 360;
            ctx.fill(x, 30, x + 2, 31, hsv(hue, 1f, 1f, 200));
        }

        int startX = baseX();
        int startY = 38;
        for (int ci = 0; ci < CATS.length; ci++) {
            Category cat = CATS[ci];
            int px = startX + ci * (PW + GAP);
            if (px + PW < 0 || px > width) continue;

            float slideIn = Math.min(1f, t * 5 - ci * 0.12f);
            if (slideIn <= 0) continue;
            int pySl = (int) (startY - 25 * (1 - slideIn));
            int al = (int) (slideIn * 200);

            ctx.fill(px, pySl, px + PW, pySl + PH, al << 24 | 0x04000C);
            int catHue = ((int) (t * 45 + ci * 60)) % 360;
            int borderColor = hsv(catHue, 0.9f, 1f, al);
            ctx.fill(px, pySl, px + PW, pySl + 1, borderColor);
            ctx.fill(px, pySl + PH - 1, px + PW, pySl + PH, borderColor);
            ctx.fill(px, pySl, px + 1, pySl + PH, borderColor);
            ctx.fill(px + PW - 1, pySl, px + PW, pySl + PH, borderColor);
            ctx.fill(px + 1, pySl + 1, px + PW - 1, pySl + CHH, hsv(catHue, 0.5f, 0.5f, al));
            ctx.drawCenteredTextWithShadow(textRenderer, cat.name, px + PW / 2, pySl + (CHH - 8) / 2 + 1, borderColor);

            List<Module> mods = CosmosClient.moduleManager.getByCategory(cat);
            int modY = pySl + CHH + 1;
            for (Module mod : mods) {
                if (modY + MH > pySl + PH - 1) break;
                boolean hov = selectedModule == null && mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH;
                if (mod.isEnabled()) {
                    float pulse = (float) (0.6 + 0.4 * Math.sin(t * 3 + ci));
                    ctx.fill(px + 1, modY, px + PW - 1, modY + MH, hsv(catHue, 0.8f, pulse, (int) (al * 0.55)));
                    ctx.fill(px + 1, modY, px + 3, modY + MH, borderColor);
                } else if (hov) {
                    ctx.fill(px + 1, modY, px + PW - 1, modY + MH, 0x22FFFFFF);
                }
                int tc = mod.isEnabled() ? 0xFFFFFFFF : (hov ? 0xFFCCCCCC : 0xFF777777);
                ctx.drawTextWithShadow(textRenderer, mod.name, px + 7, modY + (MH - 8) / 2 + 1, tc);
                if (!mod.getSettings().isEmpty()) {
                    ctx.fill(px + PW - 8, modY + MH / 2 - 1, px + PW - 5, modY + MH / 2 + 2, hov ? borderColor : 0xFF444444);
                }
                modY += MH;
            }
        }

        if (selectedModule == null) {
            for (int ci = 0; ci < CATS.length; ci++) {
                int px = startX + ci * (PW + GAP);
                List<Module> mods = CosmosClient.moduleManager.getByCategory(CATS[ci]);
                int modY = startY + CHH + 1;
                for (Module mod : mods) {
                    if (modY + MH > startY + PH - 1) break;
                    if (mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH)
                        drawTooltip(ctx, mod.description, mx, my);
                    modY += MH;
                }
            }
        }

        drawScrollBar(ctx, t);

        if (selectedModule != null) {
            ctx.fill(0, 0, width, height, 0xB8020008);
            drawSettingsPanel(ctx, mx, my, t);
        }

        super.render(ctx, mx, my, delta);
    }

    private void drawScrollBar(DrawContext ctx, float t) {
        int y1 = height - 10, y2 = height - 4;
        int x1 = 8, x2 = width - 8;
        ctx.fill(x1, y1, x2, y2, 0x44FFFFFF);
        float ms = maxScroll();
        int track = x2 - x1;
        int thumbW = ms <= 0 ? track : Math.max(24, (int) (track * ((width - 16f) / totalPanelsWidth())));
        int thumbX = ms <= 0 ? x1 : x1 + (int) ((track - thumbW) * (scrollX / ms));
        ctx.fill(thumbX, y1, thumbX + thumbW, y2, hsv(((int) (t * 45)) % 360, 0.9f, 1f, 230));
    }

    private void drawSettingsPanel(DrawContext ctx, int mx, int my, float t) {
        int px = (width - SPW) / 2, py = (height - SPH) / 2;
        int hue = ((int) (t * 45)) % 360;
        int border = hsv(hue, 0.9f, 1f, 255);

        ctx.fill(px - 1, py - 1, px + SPW + 1, py + SPH + 1, border);
        ctx.fill(px, py, px + SPW, py + SPH, 0xF6050012);
        ctx.drawCenteredTextWithShadow(textRenderer, selectedModule.name, px + SPW / 2, py + 5, border);
        ctx.fill(px + 2, py + 16, px + SPW - 2, py + 17, 0x44FFFFFF);

        boolean xh = isOverClose(mx, my);
        ctx.fill(px + SPW - 14, py + 3, px + SPW - 3, py + 14, xh ? 0xFFFF3355 : 0x66FF3355);
        ctx.drawCenteredTextWithShadow(textRenderer, "x", px + SPW - 9, py + 5, 0xFFFFFFFF);

        int sy = py + 22;
        for (var setting : selectedModule.getSettings()) {
            if (sy + 20 > py + SPH - 4) break;
            if (setting instanceof BoolSetting bs) {
                ctx.drawTextWithShadow(textRenderer, bs.name, px + 8, sy + 3, 0xFFCCCCCC);
                boolean on = bs.getValue();
                ctx.fill(px + SPW - 36, sy + 1, px + SPW - 6, sy + 13, on ? hsv(hue, 0.8f, 0.4f, 220) : 0x33FFFFFF);
                ctx.drawCenteredTextWithShadow(textRenderer, on ? "ON" : "OFF", px + SPW - 21, sy + 3, on ? border : 0xFF888888);
                sy += 18;
            } else if (setting instanceof NumberSetting ns) {
                ctx.drawTextWithShadow(textRenderer, ns.name, px + 8, sy + 1, 0xFFCCCCCC);
                String val = String.format("%.1f", ns.getValue());
                ctx.drawTextWithShadow(textRenderer, val, px + SPW - textRenderer.getWidth(val) - 8, sy + 1, border);
                int bw = SPW - 16;
                float pct = (float) ((ns.getValue() - ns.min) / (ns.max - ns.min));
                ctx.fill(px + 8, sy + 12, px + 8 + bw, sy + 16, 0x33FFFFFF);
                ctx.fill(px + 8, sy + 12, px + 8 + (int) (bw * pct), sy + 16, border);
                sy += 22;
            } else if (setting instanceof ModeSetting msn) {
                ctx.drawTextWithShadow(textRenderer, msn.name, px + 8, sy + 3, 0xFFCCCCCC);
                String v = "< " + msn.getValue() + " >";
                ctx.drawTextWithShadow(textRenderer, v, px + SPW - textRenderer.getWidth(v) - 8, sy + 3, border);
                sy += 18;
            }
        }
    }

    private boolean isOverClose(double mx, double my) {
        int px = (width - SPW) / 2, py = (height - SPH) / 2;
        return mx >= px + SPW - 14 && mx <= px + SPW - 3 && my >= py + 3 && my <= py + 14;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (selectedModule != null) {
            if (isOverClose(mx, my)) { selectedModule = null; CosmosClient.configManager.save(); return true; }
            int px = (width - SPW) / 2, py = (height - SPH) / 2;
            if (mx < px || mx > px + SPW || my < py || my > py + SPH) return true;
            int sy = py + 22;
            for (var setting : selectedModule.getSettings()) {
                if (sy + 20 > py + SPH - 4) break;
                if (setting instanceof BoolSetting bs) {
                    if (my >= sy && my <= sy + 14) { bs.toggle(); CosmosClient.configManager.save(); }
                    sy += 18;
                } else if (setting instanceof NumberSetting ns) {
                    if (my >= sy + 9 && my <= sy + 19) {
                        double pct = Math.max(0, Math.min(1, (mx - (px + 8)) / (SPW - 16)));
                        ns.setValue(round1(ns.min + pct * (ns.max - ns.min)));
                        CosmosClient.configManager.save();
                    }
                    sy += 22;
                } else if (setting instanceof ModeSetting msn) {
                    if (my >= sy && my <= sy + 14) { msn.cycle(); CosmosClient.configManager.save(); }
                    sy += 18;
                }
            }
            return true;
        }

        if (my >= height - 14) {
            draggingScroll = true;
            updateScrollFromMouse(mx);
            return true;
        }

        int startX = baseX(), startY = 38;
        for (int ci = 0; ci < CATS.length; ci++) {
            int px = startX + ci * (PW + GAP);
            List<Module> mods = CosmosClient.moduleManager.getByCategory(CATS[ci]);
            int modY = startY + CHH + 1;
            for (Module mod : mods) {
                if (modY + MH > startY + PH - 1) break;
                if (mx >= px + 1 && mx <= px + PW - 1 && my >= modY && my <= modY + MH) {
                    if (button == 0) { mod.toggle(); CosmosClient.configManager.save(); }
                    else if (button == 1 && !mod.getSettings().isEmpty()) selectedModule = mod;
                    return true;
                }
                modY += MH;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    private void updateScrollFromMouse(double mx) {
        float msc = maxScroll();
        if (msc <= 0) return;
        int x1 = 8, x2 = width - 8;
        double pct = Math.max(0, Math.min(1, (mx - x1) / (double) (x2 - x1)));
        scrollX = (float) (pct * msc);
    }

    private double round1(double v) { return Math.round(v * 10.0) / 10.0; }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (draggingScroll) { updateScrollFromMouse(mx); return true; }
        if (selectedModule != null && btn == 0) {
            int px = (width - SPW) / 2, py = (height - SPH) / 2;
            int sy = py + 22;
            for (var setting : selectedModule.getSettings()) {
                if (sy + 20 > py + SPH - 4) break;
                if (setting instanceof NumberSetting ns) {
                    if (my >= sy + 9 && my <= sy + 19) {
                        double pct = Math.max(0, Math.min(1, (mx - (px + 8)) / (SPW - 16)));
                        ns.setValue(round1(ns.min + pct * (ns.max - ns.min)));
                    }
                    sy += 22;
                } else sy += 18;
            }
            return true;
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (draggingScroll || selectedModule != null) CosmosClient.configManager.save();
        draggingScroll = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (selectedModule == null) {
            scrollX = Math.max(0, Math.min(maxScroll(), scrollX - (float) amount * 30));
            return true;
        }
        return super.mouseScrolled(mx, my, amount);
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
