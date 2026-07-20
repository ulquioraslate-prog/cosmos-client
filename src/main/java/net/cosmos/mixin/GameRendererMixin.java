package net.cosmos.mixin;

import net.cosmos.CosmosClient;
import net.cosmos.module.modules.utility.Timer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    // Timer mixin would modify tick rate here
    // FullBright/gamma handled directly via options
}
