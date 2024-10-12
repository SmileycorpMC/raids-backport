package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class MansionConfig {
    
    public static boolean allayJails;
    public static boolean ominousBottles;
    public static boolean superOminousBottles;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/mansions.cfg"));
        try {
            config.load();
            allayJails = config.get("entities", "allayJails", true, "Do allays spawn in Woodland Mansion jails?").getBoolean();
            ominousBottles = config.get("loot", "ominousBottles", true, "Can ominous bottles be found in mansion chests?").getBoolean();
            superOminousBottles = config.get("loot", "superOminousBottles", true, "Can higher powered than usual ominous bottles be found in mansion chests?").getBoolean();
        } catch (Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
