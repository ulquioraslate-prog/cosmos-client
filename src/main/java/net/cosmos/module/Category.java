package net.cosmos.module;

public enum Category {
    SURVIVAL("Survival", 0xFF00FFAA),
    BUILDING("Building", 0xFF00AAFF),
    COMBAT  ("Combat",   0xFFFF4444),
    MOVEMENT("Movement", 0xFFFFAA00),
    RENDER  ("Render",   0xFFAA44FF),
    UTILITY ("Utility",  0xFF888888);

    public final String name;
    public final int color;
    Category(String n, int c) { name = n; color = c; }
}
