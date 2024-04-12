package net.smileycorp.raids.common;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEvoker;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.smileycorp.raids.common.command.CommandDebugRaid;
import net.smileycorp.raids.common.command.CommandSpawnPatrol;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.data.RaidHandler;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.world.MapGenOutpost;
import net.smileycorp.raids.common.world.RaidsWorldGenerator;
import net.smileycorp.raids.common.world.StructureOutpostPieces;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.config.OutpostConfig;
import net.smileycorp.raids.config.PatrolConfig;
import net.smileycorp.raids.config.RaidConfig;
import net.smileycorp.raids.integration.ModIntegration;

import java.util.Map;
import java.util.Random;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		EntityConfig.syncConfig(event);
		OutpostConfig.syncConfig(event);
		PatrolConfig.syncConfig(event);
		RaidConfig.syncConfig(event);
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
		registerSpawns();
		registerRaidBuffs();
		ModIntegration.init();
	}
	
	private void registerRaidBuffs() {
		RaidHandler.registerRaidBuffs(ItemBow.class, CommonProxy::applyBowBuffs);
		RaidHandler.registerRaidBuffs(ItemSword.class, CommonProxy::applyMeleeBuffs);
		RaidHandler.registerRaidBuffs(ItemTool.class, CommonProxy::applyMeleeBuffs);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandDebugRaid());
		event.registerServerCommand(new CommandSpawnPatrol());
	}
	
	private void registerSpawns() {
		RaidHandler.registerEntry(EntityPillager.class, new int[]{0, 4, 3, 3, 4, 4, 4, 2}, 0.06f, null, null);
		RaidHandler.registerEntry(EntityVindicator.class, new int[]{0, 0, 2, 0, 1, 4, 2, 5}, 0.06f, null, (difficulty, rand, raid, wave, isBonusWave) ->
				difficulty == EnumDifficulty.EASY ? rand.nextInt(2) : difficulty == EnumDifficulty.NORMAL ? 1 : 2);
		RaidHandler.registerEntry(EntityRavager.class, new int[]{0, 0, 1, 0, 0, 0, 0}, 0, (raid, world, numSpawned) -> {
			int i = raid.getGroupsSpawned() + 1;
			if (i == raid.getNumGroups(EnumDifficulty.NORMAL)) return new EntityPillager(world);
			if (i >= raid.getNumGroups(EnumDifficulty.HARD)) return numSpawned.containsKey(EntityEvoker.class) ? new EntityVindicator(world) : new EntityEvoker(world);
			return null;
		}, (difficulty, rand, raid, wave, isBonusWave) -> difficulty != EnumDifficulty.EASY && isBonusWave ? 1 : 0);
		RaidHandler.registerEntry(EntityWitch.class, new int[]{0, 0, 0, 0, 3, 0, 0, 1}, 0, null, (difficulty, rand, raid, wave, isBonusWave)  ->
			(difficulty == EnumDifficulty.EASY || wave <= 2 || wave == 4) ? 0 : 1);
		RaidHandler.registerEntry(EntityEvoker.class, new int[]{0, 0, 0, 1, 0, 1, 0, 2}, 0.06f, null, null);
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
