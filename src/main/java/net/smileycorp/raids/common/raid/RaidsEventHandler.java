package net.smileycorp.raids.common.raid;

import ibxm.Player;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.Raids;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.raid.capabilities.Raider;

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
		if (event.getEntity() instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) event.getEntity();
			villager.tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(villager, EntityLivingBase.class, RaidHandler::isRaider, 8.0F, 0.8D, 0.8D));
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
				raid.removeFromRaid(entity, false);
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
				}
			}
		}
	}

	@SubscribeEvent
	public void allowDespawn(LivingSpawnEvent.AllowDespawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.hasCapability(RaidsContent.RAIDER, null)) {
			if (entity.getCapability(RaidsContent.RAIDER, null).isRaidActive()) event.setResult(Result.DENY);
		}
	}
	
}
