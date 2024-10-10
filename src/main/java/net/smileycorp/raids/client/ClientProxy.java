package net.smileycorp.raids.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.client.entity.*;
import net.smileycorp.raids.client.particle.ParticleRaidOmen;
import net.smileycorp.raids.client.particle.ParticleVibration;
import net.smileycorp.raids.common.CommonProxy;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.config.ClientConfig;

@EventBusSubscriber(value = Side.CLIENT, modid= Constants.MODID)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ClientConfig.syncConfig(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityPillager.class, RenderPillager::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRavager.class, RenderRavager::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAllay.class, RenderAllay::new);
		if (ClientConfig.newVexModel) RenderingRegistry.registerEntityRenderingHandler(EntityVex.class, RenderRaidsVex::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVindicator.class, RenderRaidsVindicator::new);
		for (int i = 0; i < 255; i++) ModelLoader.setCustomModelResourceLocation(RaidsContent.OMINOUS_BOTTLE, i, new ModelResourceLocation(Constants.loc("ominous_bottle"), "normal"));
	}
	
	@SubscribeEvent
	public static void mapTextures(TextureStitchEvent event) {
		TextureMap map = event.getMap();
		ParticleRaidOmen.SPRITE = map.registerSprite(Constants.loc("particle/raid_omen"));
		ParticleVibration.SPRITE = map.registerSprite(Constants.loc("particle/vibration"));
	}
	
}
