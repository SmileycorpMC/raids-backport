package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class IntegrationConfig {

    public static boolean crossbowsBackport = true;
    public static boolean crossbow = true;
    public static boolean spartanWeaponry = true;
    public static boolean tconstruct = true;
    public static boolean futuremc = true;
    public static boolean tektopia = true;
    public static boolean deeperDepths = true;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/integration.cfg"));
        try {
            config.load();
            crossbowsBackport = config.get("general", "crossbowsBackport", true, "Enable mod integration with Crossbows Backport (Only works if the mod is installed)").getBoolean();
            crossbow = config.get("general", "crossbow", true, "Enable mod integration with crossbow (Only works if the mod is installed)").getBoolean();
            spartanWeaponry = config.get("general", "spartanWeaponry", true, "Enable mod integration with Spartan Weaponry (Only works if the mod is installed)").getBoolean();
            tconstruct = config.get("general", "tconstruct", true, "Enable mod integration with Tinker's Construct (Only works if the mod is installed)").getBoolean();
            futuremc = config.get("general", "futuremc", true, "Enable mod integration with FutureMC (Only works if the mod is installed)").getBoolean();
            tektopia = config.get("general", "tektopia", true, "Enable mod integration with Tektopia (Only works if the mod is installed)").getBoolean();
            deeperDepths = config.get("general", "deeperDepths", true, "Enable mod integration with Deeper Depths (Only works if the mod is installed)").getBoolean();
        } catch (Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
