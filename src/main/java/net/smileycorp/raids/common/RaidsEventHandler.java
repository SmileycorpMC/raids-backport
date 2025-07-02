package net.smileycorp.raids.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.raids.common.entities.ai.EntityAIGiveGift;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import net.smileycorp.raids.common.items.ItemOminousBottle;
import net.smileycorp.raids.common.raid.*;
import net.smileycorp.raids.common.util.accessors.ILootPool;
import net.smileycorp.raids.common.world.WorldDataOutposts;
import net.smileycorp.raids.common.world.WorldGenOutpost;
import net.smileycorp.raids.config.MansionConfig;
import net.smileycorp.raids.config.OutpostConfig;
import net.smileycorp.raids.config.RaidConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbow.CrossbowIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;
import net.smileycorp.raids.integration.deeperdepths.DeeperDepthsIntegration;
import net.smileycorp.raids.integration.spartanweaponry.SpartanWeaponryIntegration;
import net.smileycorp.raids.integration.tconstruct.TinkersConstructIntegration;

import java.util.List;

public class RaidsEventHandler {

	@SubscribeEvent
	public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (!(entity instanceof EntityLiving) || entity == null) return;
		if (entity.world == null) return;
		if (entity.world.isRemote) return;
		if (RaidHandler.isRaider(entity)) event.addCapability(Constants.loc("Raider"), new Raider.Provider((EntityLiving) entity));
		if (entity instanceof EntityPlayer && RaidConfig.raidCenteredOnPlayer) event.addCapability(Constants.loc("RaidOmen"), new RaidOmenTracker.Provider());
	}
	
	@SubscribeEvent
	public void onAddedToWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof EntityVillager)) return;
		EntityVillager villager = (EntityVillager) entity;
		villager.tasks.addTask(1, new EntityAIAvoidEntity<>(villager, EntityLivingBase.class, RaidHandler::isRaider, 16.0F, 0.8D, 0.8D));
		villager.tasks.addTask(3, new EntityAIGiveGift(villager));
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.world instanceof WorldServer) WorldDataRaids.getData((WorldServer) event.world).tick();
	}
	
	@SubscribeEvent
	public void livingTick(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;
		if (entity.world.isRemote) return;
		if (entity.ticksExisted % 100 == 0 && entity.getRNG().nextInt(2) == 0 && RaidConfig.isTickableVillager(entity))
			entity.world.getVillageCollection().addToVillagerPositionList(entity.getPosition());
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;
		World world = entity.world;
		if (world.isRemote |!(entity instanceof EntityLiving)) return;
		if (!entity.hasCapability(RaidsContent.RAIDER, null)) return;
		Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
		if (raider.hasActiveRaid()) raider.getCurrentRaid().updateBossbar();
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof EntityLiving)) return;
		EntityLiving entity = (EntityLiving) event.getEntityLiving();
		World world = entity.world;
		if (world.isRemote |!entity.hasCapability(RaidsContent.RAIDER, null)) return;
		Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
		if (!(world instanceof WorldServer)) return;
		Entity attacker = event.getSource().getTrueSource();
		Raid raid = raider.getCurrentRaid();
		if (raid != null) {
			if (raider.isPatrolLeader()) raid.removeLeader(raider.getWave());
			if (attacker instanceof EntityPlayer) raid.addHeroOfTheVillage(attacker);
			raid.removeFromRaid(entity, true);
		}
		if (!raider.isPatrolLeader() || raid != null || WorldDataRaids.getData((WorldServer) world).getRaidAt(entity.getPosition()) != null) return;
		ItemStack itemstack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		EntityPlayer player = null;
		if (attacker instanceof EntityPlayer) player = (EntityPlayer) attacker;
		else if (attacker instanceof EntityTameable) {
			EntityLivingBase owner = ((EntityTameable) attacker).getOwner();
			if (owner instanceof EntityPlayer) player = (EntityPlayer) owner;
		}
		if (itemstack.isEmpty() |! ItemStack.areItemStacksEqual(itemstack, Constants.ominousBanner()) || player == null) return;
		if (player instanceof EntityPlayerMP) RaidsAdvancements.VOLUNTARY_EXILE.trigger((EntityPlayerMP) player);
		if (!RaidConfig.ominousBottles) {
			PotionEffect effect = player.getActivePotionEffect(RaidsContent.BAD_OMEN);
			int i = 1;
			if (effect != null) i += effect.getAmplifier();
			else i--;
			i = MathHelper.clamp(i, 0, 4);
			player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 120000, i, false, true));
			return;
		}
		entity.entityDropItem(ItemOminousBottle.createStack(entity.getRNG().nextInt(5)),0);
	}
	
	@SubscribeEvent
	public void potionAdded(PotionEvent.PotionAddedEvent event) {
		if (!RaidConfig.ominousBottles) return;
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.world.isRemote) return;
		if (event.getOldPotionEffect() != null) return;
		Potion effect = event.getPotionEffect().getPotion();
		SoundEvent sound = effect == RaidsContent.BAD_OMEN ? RaidsSoundEvents.BAD_OMEN : effect == RaidsContent.RAID_OMEN ? RaidsSoundEvents.RAID_OMEN : null;
		if (sound != null) entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, entity.getSoundCategory(), 1.0F, 1.0F);
	}
	
	@SubscribeEvent
	public void potionExpired(PotionEvent.PotionExpiryEvent event) {
		if (!RaidConfig.ominousBottles) return;
		if (event.getPotionEffect().getPotion() != RaidsContent.RAID_OMEN) return;
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP)) return;
		EntityPlayerMP player = (EntityPlayerMP)entity;
		if (player.isSpectator()) return;
		World world = player.world;
		if (world.getDifficulty() == EnumDifficulty.PEACEFUL) return;
		if (Raid.isVillage(world, RaidConfig.raidCenteredOnPlayer ? RaidOmenTracker.getRaidStart(player) : player.getPosition()))
			WorldDataRaids.getData((WorldServer) world).createOrExtendRaid(player);
	}
	
	@SubscribeEvent
	public void checkSpawn(LivingSpawnEvent.CheckSpawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isCreatureType(EnumCreatureType.MONSTER, true) || event.isSpawner()) return;
		if (!OutpostConfig.isSpawnEntity(entity)) return;
		World world = event.getWorld();
		if (world.isRemote) return;
		BlockPos pos = entity.getPosition();
		if (Raid.isVillage(world, pos)) return;
		WorldGenOutpost.OutpostStart structure = WorldDataOutposts.getData((WorldServer) world).getStructureAt(pos);
		if (structure == null) return;
		AxisAlignedBB aabb = structure.getSpawnBox();
		if (aabb == null) return;
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, OutpostConfig::isSpawnEntity);
		event.setResult(entities.size() < OutpostConfig.maxEntities && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 8
				&& world.getBlockState(pos.down()).isOpaqueCube() ? Result.ALLOW : Result.DENY);
	}
	
	@SubscribeEvent
	public void spawn(LivingSpawnEvent.SpecialSpawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.hasCapability(RaidsContent.RAIDER, null)) return;
		if (!RaidHandler.isRaider(entity)) return;
		Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
		if (raider.hasActiveRaid() || raider.isPatrolling()) return;
		float chance = raider.getCaptainChance();
		if (chance <= 0) return;
		if (entity.getRNG().nextFloat() > chance) return;
		raider.setPatrolLeader(true);
		entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, Constants.ominousBanner());
	}

	@SubscribeEvent
	public void allowDespawn(LivingSpawnEvent.AllowDespawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.hasCapability(RaidsContent.RAIDER, null)) return;
		Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
		if (raider.hasActiveRaid() || raider.isPatrolling()) event.setResult(Result.DENY);
	}
	
	@SubscribeEvent
	public void getSpawns(WorldEvent.PotentialSpawns event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		if (event.getType() != EnumCreatureType.MONSTER) return;
		if (!WorldDataOutposts.getData((WorldServer) world).isInOutpost(event.getPos())) return;
		List<Biome.SpawnListEntry> spawns = event.getList();
		spawns.clear();
		spawns.addAll(OutpostConfig.getSpawnEntities());
	}
	
	@SubscribeEvent
	public void addTrades(MerchantTradeOffersEvent event) {
		if (!(event.getMerchant() instanceof EntityVillager) || event.getList() == null || event.getPlayer() == null) return;
		EntityPlayer player = event.getPlayer();
		if (!player.isPotionActive(RaidsContent.HERO_OF_THE_VILLAGE)) return;
		int amplifier = player.getActivePotionEffect(RaidsContent.HERO_OF_THE_VILLAGE).getAmplifier();
		MerchantRecipeList newList = new MerchantRecipeList();
		for (MerchantRecipe recipe : event.getList()) {
			MerchantRecipe newRecipe = new MerchantRecipe(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), recipe.getItemToSell(), recipe.getToolUses(), recipe.getMaxTradeUses());
			ITradeDiscount trade = (ITradeDiscount) newRecipe;
			double d0 = 0.3D + 0.0625D * (double)amplifier;
			int j = (int)Math.floor(d0 * (double)newRecipe.getItemToBuy().getCount());
			trade.setDiscountedPrice(Math.max(j, 1));
			newList.add(newRecipe);
		}
		event.setList(newList);
	}
	
	@SubscribeEvent
	public void addLoot(LootTableLoadEvent event) {
		if (Constants.OUTPOST_CHESTS.equals(event.getName())) {
			LootTable table = event.getTable();
			if (RaidConfig.ominousBottles && OutpostConfig.ominousBottles) {
				LootPool bottlePool = table.getPool("raids:ominous_bottle");
				bottlePool.addEntry(new LootEntryItem(ModIntegration.DEEPER_DEPTHS_LOADED ? DeeperDepthsIntegration.getOminousBottle() :
						RaidsContent.OMINOUS_BOTTLE, 1, 1, new LootFunction[] {new SetMetadata(new LootCondition[0], new RandomValueRange(0, 4))}, new LootCondition[0], "raids:ominous_bottle"));
			}
			if (ModIntegration.CROSSBOWS_BACKPORT_LOADED && OutpostConfig.crossbowsBackportCrossbows) CrossbowsBackportIntegration.addLoot(table);
			if (ModIntegration.CROSSBOW_LOADED && OutpostConfig.crossbowCrossbows) CrossbowIntegration.addLoot(table);
			if (ModIntegration.SPARTAN_LOADED && OutpostConfig.spartansWeaponryCrossbows) SpartanWeaponryIntegration.addLoot(table);
			if (ModIntegration.TINKERS_LOADED && OutpostConfig.tinkersConstructCrossbows) TinkersConstructIntegration.addLoot(table);
			LootPool crossbowPool = table.getPool("raids:outpost_crossbow");
			if (((ILootPool)crossbowPool).isEmpty()) crossbowPool.addEntry(new LootEntryItem(Items.BOW, 1, 1, new LootFunction[0], new LootCondition[0], "raids:bow"));
		}
		if (LootTableList.CHESTS_WOODLAND_MANSION.equals(event.getName())) {
			LootTable table =  event.getTable();
			if (RaidConfig.ominousBottles && MansionConfig.ominousBottles) {
				if (table.pools.size() >= 3) table.pools.get(2).addEntry(new LootEntryItem(ModIntegration.DEEPER_DEPTHS_LOADED ? DeeperDepthsIntegration.getOminousBottle() :
						RaidsContent.OMINOUS_BOTTLE, 6, 1, new LootFunction[] {new SetMetadata(new LootCondition[0], new RandomValueRange(2, 4))},
						new LootCondition[0], "raids:ominous_bottle"));
			}
			if (RaidConfig.ominousBottles && MansionConfig.superOminousBottles) {
				event.getTable().addPool(new LootPool(new LootEntry[]{new LootEntryItem(ModIntegration.DEEPER_DEPTHS_LOADED ? DeeperDepthsIntegration.getOminousBottle() :
						RaidsContent.OMINOUS_BOTTLE, 1, 1, new LootFunction[] {new SetMetadata(new LootCondition[0], new RandomValueRange(5, 7))},
						new LootCondition[0], "raids:ominous_bottle")}, new LootCondition[]{new RandomChance(0.25f)},
						new RandomValueRange(1), new RandomValueRange(0), "raids:super_ominous_bottle"));
			}
		}
	}
	
	@SubscribeEvent
	public void remapItems(RegistryEvent.MissingMappings<Item> event) {
		if (ModIntegration.DEEPER_DEPTHS_LOADED) return;
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
			if (mapping.key.equals(new ResourceLocation("deeperdepths:ominous_bottle"))) {
				mapping.remap(RaidsContent.OMINOUS_BOTTLE);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void remapEffects(RegistryEvent.MissingMappings<Potion> event) {
		if (ModIntegration.DEEPER_DEPTHS_LOADED) return;
		for (RegistryEvent.MissingMappings.Mapping<Potion> mapping : event.getAllMappings()) {
			if (mapping.key.equals(new ResourceLocation("deeperdepths:bad_omen"))) {
				mapping.remap(RaidsContent.BAD_OMEN);
				return;
			}
		}
	}
	
}
