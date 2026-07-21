package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.combat.AntiKnockback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void cosmos$antiKnockback(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c == null || c.player == null || CosmosClient.moduleManager == null) return;
        if (packet.getId() != c.player.getId()) return;
        AntiKnockback ak = (AntiKnockback) CosmosClient.moduleManager.get(AntiKnockback.class);
        if (ak != null && ak.isEnabled()) ci.cancel();
    }
}
