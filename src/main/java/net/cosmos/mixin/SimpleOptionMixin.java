package net.cosmos.mixin;

import net.cosmos.util.ISimpleOption;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleOption.class)
public abstract class SimpleOptionMixin<T> implements ISimpleOption<T> {
    @Shadow T value;

    @Override
    public void cosmos$forceSetValue(T v) {
        this.value = v;
    }
}
