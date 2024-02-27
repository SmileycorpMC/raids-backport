package net.smileycorp.raids.integration;

import net.minecraftforge.fml.common.Loader;

public class ModIntegration {
    public static boolean CROSSBOWS_LOADED = Loader.isModLoaded("crossbows");
    public static boolean CROSSBOW_LOADED = Loader.isModLoaded("crossbow");
    public static boolean SPARTAN_LOADED = Loader.isModLoaded("spartanweaponry");
    public static boolean TINKERS_LOADED = Loader.isModLoaded("tconstruct");
    public static boolean FUTUREMC_LOADED = Loader.isModLoaded("futuremc");
    
}
