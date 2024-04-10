package net.smileycorp.raids.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.smileycorp.raids.common.util.RaidsLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES)
public class Raids {
	
	private static Logger logger = LogManager.getLogger(Constants.NAME);
	
	@SidedProxy(clientSide = Constants.CLIENT, serverSide = Constants.SERVER)
	public static CommonProxy proxy;
	
	public Raids() {
		RaidsLogger.clearLog();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
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
