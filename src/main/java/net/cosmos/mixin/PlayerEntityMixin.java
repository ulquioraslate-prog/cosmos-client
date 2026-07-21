package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.movement.SafeWalk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    private void cosmos$safeWalk(CallbackInfoReturnable<Boolean> cir) {
        if (CosmosClient.moduleManager == null) return;
        if ((Object) this != MinecraftClient.getInstance().player) return;
        SafeWalk sw = (SafeWalk) CosmosClient.moduleManager.get(SafeWalk.class);
        if (sw != null && sw.isEnabled()) cir.setReturnValue(true);
    }
}
