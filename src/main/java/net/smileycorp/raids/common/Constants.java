package net.smileycorp.raids.common;

import net.minecraft.util.ResourceLocation;

public class Constants {
	
	public static final String NAME = "Raids";
	public static final String MODID = "raids";
	public static final String VERSION = "1.1.3b";
	public static final String DEPENDENCIES = "after:crossbows;after:futuremc";
	public static final String PATH = "net.smileycorp.raids.";
	public static final String CLIENT = PATH + "client.ClientProxy";
	public static final String SERVER = PATH + "common.CommonProxy";
	
	public static final ResourceLocation PILLAGER_DROPS = loc("entities/pillager");
	public static final ResourceLocation RAVAGER_DROPS = loc("entities/ravager");
	public static final ResourceLocation OUTPOST_CHESTS = loc("chests/pillager_outpost");
    
    public static String name(String name) {
		return MODID + "." + name.replace("_", "");
	}
	
	public static ResourceLocation loc(String name) {
		return new ResourceLocation(MODID, name.toLowerCase());
	}

}
