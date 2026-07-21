package net.cosmos.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cosmos.CosmosClient;
import net.cosmos.module.modules.render.OreESP;
import net.cosmos.module.modules.render.Tracers;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Render3D {

    private static final List<float[]> ORE_BOXES = new ArrayList<>();
    private static int scanTimer = 0;

    public static void tick(MinecraftClient c) {
        if (CosmosClient.moduleManager == null || c.world == null || c.player == null) return;
        OreESP esp = (OreESP) CosmosClient.moduleManager.get(OreESP.class);
        if (esp == null || !esp.isEnabled()) { ORE_BOXES.clear(); return; }
        if (scanTimer-- > 0) return;
        scanTimer = 20;
        ORE_BOXES.clear();
        BlockPos pp = c.player.getBlockPos();
        int R = 24;
        for (BlockPos p : BlockPos.iterate(pp.add(-R, -R, -R), pp.add(R, R, R))) {
            Block b = c.world.getBlockState(p).getBlock();
            float[] col = oreColor(b, esp);
            if (col != null) {
                ORE_BOXES.add(new float[]{p.getX(), p.getY(), p.getZ(), col[0], col[1], col[2]});
                if (ORE_BOXES.size() >= 400) break;
            }
        }
    }

    private static float[] oreColor(Block b, OreESP esp) {
        if (esp.diamonds.getValue() && (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE)) return new float[]{0.2f, 0.9f, 1f};
        if (esp.gold.getValue() && (b == Blocks.GOLD_ORE || b == Blocks.DEEPSLATE_GOLD_ORE || b == Blocks.NETHER_GOLD_ORE)) return new float[]{1f, 0.85f, 0.1f};
        if (esp.iron.getValue() && (b == Blocks.IRON_ORE || b == Blocks.DEEPSLATE_IRON_ORE)) return new float[]{0.9f, 0.8f, 0.7f};
        if (esp.ancient.getValue() && b == Blocks.ANCIENT_DEBRIS) return new float[]{0.8f, 0.4f, 0.2f};
        if (esp.emerald.getValue() && (b == Blocks.EMERALD_ORE || b == Blocks.DEEPSLATE_EMERALD_ORE)) return new float[]{0.1f, 1f, 0.3f};
        return null;
    }

    public static void render(WorldRenderContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null || c.world == null || CosmosClient.moduleManager == null) return;
        OreESP esp = (OreESP) CosmosClient.moduleManager.get(OreESP.class);
        Tracers tr = (Tracers) CosmosClient.moduleManager.get(Tracers.class);
        boolean de = esp != null && esp.isEnabled() && !ORE_BOXES.isEmpty();
        boolean dt = tr != null && tr.isEnabled();
        if (!de && !dt) return;

        MatrixStack ms = ctx.matrixStack();
        Vec3d cam = ctx.camera().getPos();
        ms.push();
        ms.translate(-cam.x, -cam.y, -cam.z);
        Matrix4f mat = ms.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        if (de) for (float[] o : ORE_BOXES) box(buf, mat, o[0], o[1], o[2], o[3], o[4], o[5]);

        if (dt) {
            Vec3d start = cam.add(c.player.getRotationVec(ctx.tickDelta()).multiply(1.5));
            for (Entity e : c.world.getEntities()) {
                if (e == c.player) continue;
                float r, g, b;
                if (e instanceof PlayerEntity && tr.players.getValue()) { r = 1f; g = 0.2f; b = 0.2f; }
                else if (e instanceof Monster && tr.mobs.getValue()) { r = 1f; g = 0.6f; b = 0.1f; }
                else if (e instanceof ItemEntity && tr.items.getValue()) { r = 0.2f; g = 1f; b = 0.4f; }
                else continue;
                Vec3d end;
                if (tr.mode.is("Eyes")) end = e.getEyePos();
                else if (tr.mode.is("Feet")) end = e.getPos();
                else end = e.getBoundingBox().getCenter();
                line(buf, mat, (float) start.x, (float) start.y, (float) start.z,
                     (float) end.x, (float) end.y, (float) end.z, r, g, b);
            }
        }

        tess.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        ms.pop();
    }

    private static void line(BufferBuilder buf, Matrix4f m, float x1, float y1, float z1,
                             float x2, float y2, float z2, float r, float g, float b) {
        buf.vertex(m, x1, y1, z1).color(r, g, b, 0.9f).next();
        buf.vertex(m, x2, y2, z2).color(r, g, b, 0.9f).next();
    }

    private static void box(BufferBuilder buf, Matrix4f m, float x, float y, float z, float r, float g, float b) {
        float x2 = x + 1, y2 = y + 1, z2 = z + 1;
        line(buf, m, x, y, z, x2, y, z, r, g, b);
        line(buf, m, x2, y, z, x2, y, z2, r, g, b);
        line(buf, m, x2, y, z2, x, y, z2, r, g, b);
        line(buf, m, x, y, z2, x, y, z, r, g, b);
        line(buf, m, x, y2, z, x2, y2, z, r, g, b);
        line(buf, m, x2, y2, z, x2, y2, z2, r, g, b);
        line(buf, m, x2, y2, z2, x, y2, z2, r, g, b);
        line(buf, m, x, y2, z2, x, y2, z, r, g, b);
        line(buf, m, x, y, z, x, y2, z, r, g, b);
        line(buf, m, x2, y, z, x2, y2, z, r, g, b);
        line(buf, m, x2, y, z2, x2, y2, z2, r, g, b);
        line(buf, m, x, y, z2, x, y2, z2, r, g, b);
    }
}
