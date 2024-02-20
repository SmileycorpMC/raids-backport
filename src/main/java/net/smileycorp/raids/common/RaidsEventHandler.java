package net.smileycorp.raids.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.raid.WorldDataRaids;

public class RaidsEventHandler {

	@SubscribeEvent
	public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (!(entity instanceof EntityLiving) || entity == null) return;
		if (entity.world.isRemote) return;
		if (RaidHandler.isRaider(entity)) event.addCapability(Constants.loc("Raider"), new Raider.Provider((EntityLiving) entity));
	}
	
	@SubscribeEvent
	public void onAddedToWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) entity;
			villager.tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(villager, EntityLivingBase.class, RaidHandler::isRaider, 16.0F, 0.8D, 0.8D));
		}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.world instanceof WorldServer) {
			WorldDataRaids.getData((WorldServer) event.world).tick();
		}
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
		if (world instanceof WorldServer) {
			Entity attacker = event.getSource().getTrueSource();
			Raid raid = raider.getCurrentRaid();
			if (raid != null) {
				if (raider.isPatrolLeader()) raid.removeLeader(raider.getWave());
				if (attacker instanceof EntityPlayer) raid.addHeroOfTheVillage(attacker);
				raid.removeFromRaid(entity, true);
			}
			if (raider.isPatrolLeader() && raid == null && WorldDataRaids.getData((WorldServer) world).getRaidAt(entity.getPosition()) == null) {
				ItemStack itemstack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				EntityPlayer player = null;
				if (attacker instanceof EntityPlayer) player = (EntityPlayer) attacker;
				else if (attacker instanceof EntityTameable) {
					EntityLivingBase owner = ((EntityTameable) attacker).getOwner();
					if (owner instanceof EntityPlayer) player = (EntityPlayer) owner;
				}
				if (!itemstack.isEmpty() && ItemStack.areItemStacksEqual(itemstack, RaidsContent.createOminousBanner()) && player != null) {
					PotionEffect effect = player.getActivePotionEffect(RaidsContent.BAD_OMEN);
					int i = 1;
					if (effect != null) i += effect.getAmplifier();
					else i--;
					i = MathUtils.clamp(i, 0, 4);
					player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 120000, i, false, true));
					if (player instanceof EntityPlayerMP) RaidsContent.VOLUNTARY_EXILE.trigger((EntityPlayerMP) player);
				}
			}
		}
	}

	@SubscribeEvent
	public void allowDespawn(LivingSpawnEvent.AllowDespawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.hasCapability(RaidsContent.RAIDER, null)) {
			Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
			if (raider.hasActiveRaid() || raider.isPatrolling()) event.setResult(Result.DENY);
		}
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
			trade.setDiscountedPrice(-Math.max(j, 1));
			RaidsLogger.logInfo(trade.getDiscountedPrice());
			newList.add(newRecipe);
		}
		event.setList(newList);
	}
	
}
