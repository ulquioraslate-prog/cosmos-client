package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.movement.SafeWalk;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void cosmos$safeWalk(CallbackInfo ci) {
        if (CosmosClient.moduleManager == null) return;
        var sw = (SafeWalk) CosmosClient.moduleManager.get(SafeWalk.class);
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        if (sw != null && sw.isEnabled() && self.isOnGround()) {
            self.setSneaking(true);
        }
    }
}
