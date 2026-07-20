package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.survival.TreeCapitator;
import net.cosmos.module.modules.survival.VeinMiner;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class BlockBreakMixin {
    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void cosmos$onBlockUpdate(BlockUpdateS2CPacket pkt, CallbackInfo ci) {
        if (CosmosClient.moduleManager == null) return;
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || !pkt.getState().isAir()) return;
        BlockPos pos = pkt.getPos();
        for (var m : CosmosClient.moduleManager.getModules()) {
            if (m instanceof TreeCapitator tc && tc.isEnabled()) tc.onLogBroken(mc, pos);
            if (m instanceof VeinMiner vm      && vm.isEnabled()) vm.onOreBroken(mc, pos);
        }
    }
}
