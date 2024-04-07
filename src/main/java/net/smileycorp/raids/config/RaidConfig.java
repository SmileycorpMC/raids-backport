package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class RaidConfig {
    
    private static Configuration config;
    
    public static boolean ominousBottles = true;
    public static boolean raidCenteredOnPlayer = true;
    public static int easyWaves = 3;
    public static int normalWaves = 5;
    public static int hardWaves = 7;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/raids.cfg"));
        try{
            config.load();
            ominousBottles = config.get("general", "ominousBottles", true, "Use ominous bottles and the version of bad omen from 1.21+?").getBoolean();
            raidCenteredOnPlayer = config.get("general", "raidCenteredOnPlayer", true, "Should raids be centered on the player's location when the raid starts instead of the village center? (1.21+ behaviour)").getBoolean();
            easyWaves = config.get("general", "easyWaves", 3, "How many waves do raids last for on easy mode?").getInt();
            normalWaves = config.get("general", "normalWaves", 5, "How many waves do raids last for on normal mode?").getInt();
            hardWaves = config.get("general", "hardWaves", 7, "How many waves do raids last for on hard mode?").getInt();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
        
    }
    
}