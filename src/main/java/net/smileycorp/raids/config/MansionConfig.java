package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class MansionConfig {
    
    public static boolean allayJails;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/mansions.cfg"));
        try {
            config.load();
            allayJails = config.get("entities", "allayJails", true, "Do allays spawn in Woodland Mansion jails?").getBoolean();
        } catch (Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
