package net.cosmos.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    // SafeWalk moved to PlayerEntityMixin (clipAtLedge)
}
