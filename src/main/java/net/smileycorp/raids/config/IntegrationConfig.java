package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class IntegrationConfig {
    private static Configuration config;

    public static boolean crossbows = true;
    public static boolean crossbow = true;
    public static boolean spartanweaponry = true;
    public static boolean tconstruct = true;
    public static boolean futuremc = true;
    public static boolean tektopia = true;

    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/integration.cfg"));
        try {
            config.load();
            crossbows = config.get("general", "crossbows", true, "Only works if crossbows mod is installed").getBoolean();
            crossbow = config.get("general", "crossbow", true, "Only works if crossbow mod is installed").getBoolean();
            spartanweaponry = config.get("general", "spartanweaponry", true, "Only works if spartanweaponry mod is installed").getBoolean();
            tconstruct = config.get("general", "tconstruct", true, "Only works if tconstruct mod is installed").getBoolean();
            futuremc = config.get("general", "futuremc", true, "Only works if futuremc mod is installed").getBoolean();
            tektopia = config.get("general", "tektopia", true, "Only works if tektopia mod is installed").getBoolean();
        } catch (Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
