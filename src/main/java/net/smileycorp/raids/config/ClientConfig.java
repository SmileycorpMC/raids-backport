package net.smileycorp.raids.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ClientConfig {
    
    private static Configuration config;
    
    public static boolean newVexModel;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/client.cfg"));
        try{
            config.load();
            newVexModel = config.get("general", "newVexModel", true, "Use the new vex model from 1.19.3?").getBoolean();;
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
