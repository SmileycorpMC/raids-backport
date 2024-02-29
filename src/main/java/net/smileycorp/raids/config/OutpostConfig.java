package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class OutpostConfig {
    
    private static Configuration config;
    
    public static int maxDistance;
    public static int chance = 1;
    public static int distanceFromVillage;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/outposts.cfg"));
        try{
            config.load();
            maxDistance = config.get("generation", "maxDistance", 32, "Maximum chunk distance between two watchtowers, the lower the number the more likely the generation.").getInt();
            //chance = config.get("generation", "chance", 5, "The chance for a watchtower to spawn, the lower the number the more likely.").getInt();
            distanceFromVillage = config.get("generation", "distanceFromVillage", 160, "How close can outposts be to villages.").getInt();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
