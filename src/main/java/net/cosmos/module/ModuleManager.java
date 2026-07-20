package net.cosmos.module;

import net.cosmos.module.modules.combat.*;
import net.cosmos.module.modules.movement.*;
import net.cosmos.module.modules.render.*;
import net.cosmos.module.modules.survival.*;
import net.cosmos.module.modules.building.*;
import net.cosmos.module.modules.utility.*;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Survival
        register(new TreeCapitator());
        register(new VeinMiner());
        register(new SmartPlace());
        register(new AutoRestock());
        register(new AutoEat());
        register(new NoFall());
        register(new AutoFish());
        register(new AutoTool());
        // Building
        register(new FastBuild());
        register(new Scaffold());
        register(new AutoCraft());
        // Combat
        register(new KillAura());
        register(new AntiKnockback());
        register(new AutoTotem());
        register(new Criticals());
        // Movement
        register(new AutoSprint());
        register(new Zoom());
        register(new NoFall2());
        register(new SafeWalk());
        register(new Speed());
        register(new StepUp());
        // Render
        register(new FullBright());
        register(new OreESP());
        register(new NoWeather());
        register(new Tracers());
        register(new Chams());
        // Utility
        register(new Timer());
        register(new FPSBoost());
        register(new ClipboardCoords());
    }

    private void register(Module m) { modules.add(m); }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category c) {
        List<Module> r = new ArrayList<>();
        for (Module m : modules) if (m.category == c) r.add(m);
        return r;
    }

    public Module get(Class<?> cls) {
        for (Module m : modules) if (m.getClass() == cls) return m;
        return null;
    }

    public void onTick(MinecraftClient client) {
        for (Module m : modules) if (m.isEnabled()) m.onTick(client);
    }
}
