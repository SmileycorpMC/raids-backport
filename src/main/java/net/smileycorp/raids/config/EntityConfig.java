package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class EntityConfig {
    
    private static Configuration config;
    
    public static EntityAttributesEntry pillager;
    public static EntityAttributesEntry ravager;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
         config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/entities.cfg"));
        try{
            config.load();
            pillager = new EntityAttributesEntry(config, "pillager", 0.35, 32, 5, 24, 0, 0, 0);
            ravager = new EntityAttributesEntry(config, "ravager", 0.3, 32, 12, 100, 0, 0, 0.75);
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
