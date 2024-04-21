package net.smileycorp.raids.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.monster.EntityVex;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.client.entity.RenderPillager;
import net.smileycorp.raids.client.entity.RenderRaidsVex;
import net.smileycorp.raids.client.entity.RenderRavager;
import net.smileycorp.raids.common.CommonProxy;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
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
		if (ClientConfig.newVexModel) RenderingRegistry.registerEntityRenderingHandler(EntityVex.class, RenderRaidsVex::new);
		for (int i = 0; i < 255; i++) ModelLoader.setCustomModelResourceLocation(RaidsContent.OMINOUS_BOTTLE, i, new ModelResourceLocation(Constants.loc("ominous_bottle"), "normal"));
	}
	
}
