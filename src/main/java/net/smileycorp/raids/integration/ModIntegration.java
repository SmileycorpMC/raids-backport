package net.smileycorp.raids.integration;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.raids.integration.crossbow.CrossbowIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;
import net.smileycorp.raids.integration.spartanweaponry.SpartanWeaponryIntegration;
import net.smileycorp.raids.integration.tconstruct.TinkersConstructIntegration;

public class ModIntegration {
    public static boolean CROSSBOWS_BACKPORT_LOADED = Loader.isModLoaded("crossbows");
    public static boolean CROSSBOW_LOADED = Loader.isModLoaded("crossbow");
    public static boolean SPARTAN_LOADED = Loader.isModLoaded("spartanweaponry");
    public static boolean TINKERS_LOADED = Loader.isModLoaded("tconstruct");
    public static boolean FUTUREMC_LOADED = Loader.isModLoaded("futuremc");
    
    public static void init() {
        if (CROSSBOWS_BACKPORT_LOADED) CrossbowsBackportIntegration.init();
        if (CROSSBOW_LOADED) CrossbowIntegration.init();
        if (SPARTAN_LOADED) SpartanWeaponryIntegration.init();
        if (TINKERS_LOADED) TinkersConstructIntegration.init();
    }
    
}
