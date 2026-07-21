package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.utility.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public abstract class RenderTickCounterMixin {

    @Shadow @Final @Mutable private float tickTime;

    @Inject(method = "beginRenderTick", at = @At("HEAD"))
    private void cosmos$timer(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        if (CosmosClient.moduleManager == null) return;
        Timer t = (Timer) CosmosClient.moduleManager.get(Timer.class);
        float mult = (t != null && t.isEnabled()) ? t.getMultiplier() : 1f;
        this.tickTime = 50f / mult;
    }
}
