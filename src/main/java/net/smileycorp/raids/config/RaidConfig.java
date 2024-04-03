package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class RaidConfig {
    
    private static Configuration config;
    
    public static boolean ominousBottles = true;
    public static boolean raidCenteredOnPlayer = true;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/raids.cfg"));
        try{
            config.load();
            ominousBottles = config.get("general", "ominousBottles", true, "Use ominous bottles and the version of bad omen from 1.21+?").getBoolean();
            raidCenteredOnPlayer = config.get("general", "raidCenteredOnPlayer", true, "Should raids be centered on the player's location when the raid starts instead of the village center? (1.21+ behaviour)").getBoolean();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
        
    }
    
}
