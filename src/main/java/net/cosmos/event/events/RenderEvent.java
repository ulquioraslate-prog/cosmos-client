package net.cosmos.event.events;

import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent {
    public final MatrixStack matrices;
    public final float tickDelta;
    public RenderEvent(MatrixStack m, float d) { matrices = m; tickDelta = d; }
}
