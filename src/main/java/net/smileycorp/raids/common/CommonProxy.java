package net.smileycorp.raids.common;

import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.raids.common.capability.IRaid;
import net.smileycorp.raids.common.capability.IRaider;
import net.smileycorp.raids.common.capability.Raider;
import net.smileycorp.raids.common.capability.Raid;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RaidsEventHandler());
		CapabilityManager.INSTANCE.register(IRaid.class, new IRaid.Storage(), ()-> new Raid());
		CapabilityManager.INSTANCE.register(IRaider.class, new IRaider.Storage(), ()-> new Raider());
		registerSpawns();
	}

	public void init(FMLInitializationEvent event) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	private void registerSpawns() {
		RaidHandler.registerEntry(EntityPillager.class, new int[]{4, 3, 3, 4, 4, 4, 2}, new int[]{5, 4, 4, 5, 5, 6, 4});
		RaidHandler.registerEntry(EntityVindicator.class, new int[]{0, 2, 0, 1, 4, 2, 5}, new int[]{1, 3, 1, 2, 5, 4, 7});
		RaidHandler.registerEntry(EntityRavager.class, new int[]{0, 0, 1, 0, 0, 0, 0}, new int[]{0, 0, 1, 0, 0, 0, 0}, 
					(d, r, v, w, c, b) -> (b==true && d==EnumDifficulty.NORMAL)?r.nextInt(2)==0?2:1:c);
		RaidHandler.registerEntry(EntityWitch.class, new int[]{0, 0, 0, 3, 0, 0, 1}, new int[]{0, 0, 1, 3, 1, 1, 2});
		RaidHandler.registerEntry(EntityEvoker.class, new int[]{0, 0, 0, 0, 1, 1, 2}, new int[]{0, 0, 0, 0, 1, 1, 2});
		RaidHandler.registerEntry(EntityPillager.class, EntityRavager.class, new int[]{0, 0, 0, 0, 1, 0, 0}, new int[]{0, 0, 0, 0, 1, 0, 0}, 
					(d, r, v, w, c, b) -> (b==true && d==EnumDifficulty.NORMAL)?0:c);
		RaidHandler.registerEntry(EntityVindicator.class, EntityRavager.class, new int[]{0, 0, 0, 0, 0, 0, 1}, new int[]{0, 0, 0, 0, 0, 0, 1}, 
					(d, r, v, w, c, b) -> (b==true && d==EnumDifficulty.HARD)?r.nextInt(2)==0?2:1:c);
		RaidHandler.registerEntry(EntityVindicator.class, EntityRavager.class, new int[]{0, 0, 0, 0, 0, 0, 1}, new int[]{0, 0, 0, 0, 0, 0, 1});
	}
	
}
