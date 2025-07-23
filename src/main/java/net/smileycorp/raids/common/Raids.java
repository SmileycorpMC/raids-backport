package net.smileycorp.raids.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;


@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES)
public class Raids {
	
	@SidedProxy(clientSide = Constants.CLIENT, serverSide = Constants.SERVER)
	public static CommonProxy proxy;

	public static File CONFIG_FOLDER;
	
	public Raids() {
		RaidsLogger.clearLog();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		CONFIG_FOLDER = event.getModConfigurationDirectory().toPath().resolve(Constants.MODID).toFile();
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event){
		proxy.serverStart(event);
	}
	
}
