package net.smileycorp.raids.common;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
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
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.world.MapGenOutpost;
import net.smileycorp.raids.common.world.RaidsWorldGenerator;
import net.smileycorp.raids.common.world.StructureOutpostPieces;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.config.OutpostConfig;
import net.smileycorp.raids.config.RaidConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsIntegration;

import java.util.Map;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		EntityConfig.syncConfig(event);
		OutpostConfig.syncConfig(event);
		RaidConfig.syncConfig(event);
		PacketHandler.initPackets();
		MinecraftForge.EVENT_BUS.register(new RaidsEventHandler());
		MinecraftForge.EVENT_BUS.register(new RaidsWorldGenerator());
		MapGenStructureIO.registerStructure(MapGenOutpost.OutpostStart.class, "Pillager_Outpost");
		StructureOutpostPieces.registerStructurePieces();
		CapabilityManager.INSTANCE.register(Raider.class, new Raider.Storage(), Raider.Impl::new);
		if (RaidConfig.raidCenteredOnPlayer) CapabilityManager.INSTANCE.register(RaidOmenTracker.class, new RaidOmenTracker.Storage(), RaidOmenTracker.Impl::new);
		registerSpawns();
	}

	public void init(FMLInitializationEvent event) {
		LootTableList.register(Constants.PILLAGER_DROPS);
		LootTableList.register(Constants.RAVAGER_DROPS);
		LootTableList.register(Constants.OUTPOST_CHESTS);
		CriteriaTriggers.register(RaidsContent.WHOS_THE_PILLAGER);
		CriteriaTriggers.register(RaidsContent.VOLUNTARY_EXILE);
		CriteriaTriggers.register(RaidsContent.RAID_VICTORY);
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
		RaidHandler.registerRaidBuffs(EntityPillager.class, (entity, raid, wave, rand) -> {
			if (rand.nextFloat() <= raid.getEnchantOdds()) {
				if (ModIntegration.CROSSBOWS_LOADED) CrossbowsIntegration.applyRaidBuffs(entity, raid, wave);
			}
		});
		RaidHandler.registerRaidBuffs(EntityVindicator.class, (entity, raid, wave, rand) -> {
			ItemStack itemstack = new ItemStack(Items.IRON_AXE);
			if (rand.nextFloat() <= raid.getEnchantOdds()) {
				Map<Enchantment, Integer> map = Maps.newHashMap();
				map.put(Enchantments.SHARPNESS, wave > raid.getNumGroups(EnumDifficulty.NORMAL) ? 2 : 1);
				EnchantmentHelper.setEnchantments(map, itemstack);
			}
			entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
		});
	}
	
}
