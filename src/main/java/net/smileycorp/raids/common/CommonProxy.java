package net.smileycorp.raids.common;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.smileycorp.raids.common.command.CommandDebugRaid;
import net.smileycorp.raids.common.command.CommandFindRaiders;
import net.smileycorp.raids.common.command.CommandSpawnPatrol;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.world.MapGenOutpost;
import net.smileycorp.raids.common.world.RaidsWorldGenerator;
import net.smileycorp.raids.common.world.StructureOutpostPieces;
import net.smileycorp.raids.config.*;
import net.smileycorp.raids.config.raidevent.RaidTableLoader;
import net.smileycorp.raids.config.raidevent.conditions.ConditionRegistry;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;
import net.smileycorp.raids.integration.ModIntegration;

import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		EntityConfig.syncConfig(event);
		OutpostConfig.syncConfig(event);
		PatrolConfig.syncConfig(event);
		RaidConfig.syncConfig(event);
		RaidTableLoader.init(event);
		VillagerGiftsConfig.init(event);
		IntegrationConfig.syncConfig(event);
		PacketHandler.initPackets();
		MinecraftForge.EVENT_BUS.register(new RaidsEventHandler());
		MinecraftForge.EVENT_BUS.register(new RaidsWorldGenerator());
		MapGenStructureIO.registerStructure(MapGenOutpost.OutpostStart.class, "Pillager_Outpost");
		StructureOutpostPieces.registerStructurePieces();
		CapabilityManager.INSTANCE.register(Raider.class, new Raider.Storage(), Raider.Impl::new);
		if (RaidConfig.raidCenteredOnPlayer) CapabilityManager.INSTANCE.register(RaidOmenTracker.class, new RaidOmenTracker.Storage(), RaidOmenTracker.Impl::new);
	}

	public void init(FMLInitializationEvent event) {
		LootTableList.register(Constants.PILLAGER_DROPS);
		LootTableList.register(Constants.RAVAGER_DROPS);
		LootTableList.register(Constants.OUTPOST_CHESTS);
		CriteriaTriggers.register(RaidsContent.WHOS_THE_PILLAGER);
		CriteriaTriggers.register(RaidsContent.VOLUNTARY_EXILE);
		CriteriaTriggers.register(RaidsContent.RAID_VICTORY);
		addDefaultRaiders();
		registerRaidBuffs();
		ModIntegration.init();
		ConditionRegistry.INSTANCE.registerDefaultConditions();
		ValueRegistry.INSTANCE.registerDefaultValues();
	}
	
	private void registerRaidBuffs() {
		RaidHandler.registerRaidBuffs(ItemBow.class, CommonProxy::applyBowBuffs);
		RaidHandler.registerRaidBuffs(ItemSword.class, CommonProxy::applyMeleeBuffs);
		RaidHandler.registerRaidBuffs(ItemTool.class, CommonProxy::applyMeleeBuffs);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		RaidTableLoader.INSTANCE.loadTables();
		VillagerGiftsConfig.INSTANCE.loadGifts();
	}
	
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandDebugRaid());
		event.registerServerCommand(new CommandSpawnPatrol());
		event.registerServerCommand(new CommandFindRaiders());
	}
	
	private void addDefaultRaiders() {
		RaidHandler.addRaider(EntityPillager.class);
		RaidHandler.addRaider(EntityVindicator.class);
		RaidHandler.addRaider(EntityEvoker.class);
		RaidHandler.addRaider(EntityRavager.class);
		RaidHandler.addRaider(EntityWitch.class);
		RaidHandler.addRaider(EntityIllusionIllager.class);
	}
	
	public static ItemStack applyMeleeBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
		if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
		Map<Enchantment, Integer> map = Maps.newHashMap();
		map.put(Enchantments.SHARPNESS, wave > raid.getNumGroups(EnumDifficulty.NORMAL) ? 2 : 1);
		EnchantmentHelper.setEnchantments(map, stack);
		return stack;
	}
	
	public static ItemStack applyBowBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
		if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
		Map<Enchantment, Integer> map = Maps.newHashMap();
		if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) {
			map.put(Enchantments.POWER, 2);
			if (rand.nextInt(3) == 0) map.put(Enchantments.FLAME, 1);
		}
		else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(Enchantments.POWER, 1);
		map.put(Enchantments.PUNCH, 1);
		EnchantmentHelper.setEnchantments(map, stack);
		return stack;
	}
	
}
