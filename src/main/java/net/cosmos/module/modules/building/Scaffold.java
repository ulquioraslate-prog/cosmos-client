package net.cosmos.module.modules.building;

import net.cosmos.module.Category;
import net.cosmos.module.Module;
import net.cosmos.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    public final BoolSetting tower = addSetting(new BoolSetting("Tower", "Tower up when jumping", false));
    public Scaffold() { super("Scaffold", "Place blocks under your feet", Category.BUILDING); }
    @Override public void onTick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        if (!(c.player.getMainHandStack().getItem() instanceof BlockItem)) return;
        BlockPos below = c.player.getBlockPos().down();
        if (!c.world.getBlockState(below).isAir()) return;
        BlockPos sup = below.down();
        if (!c.world.getBlockState(sup).isAir())
            c.interactionManager.interactBlock(c.player, Hand.MAIN_HAND,
                new BlockHitResult(Vec3d.ofCenter(sup).add(0, .5, 0), Direction.UP, sup, false));
    }
}
