package net.smileycorp.raids.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.common.capability.IRaid;
import net.smileycorp.raids.common.capability.IRaider;

public class RaidsEventHandler {
	
	@SubscribeEvent
	public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (!entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null) && RaidHandler.CAPABILITY_ENTITIES.contains(entity.getClass())) {
			event.addCapability(ModDefinitions.getResource("Raider"), new IRaider.Provider((EntityLiving) entity));
		}
	}
	
	@SubscribeEvent
	public void attachVillageCapabilities(AttachCapabilitiesEvent<Village> event) {
		Village village = event.getObject();
		if (!village.hasCapability(RaidsContent.RAID_CAPABILITY, null)) {
			event.addCapability(ModDefinitions.getResource("Raid"), new IRaid.Provider(village));
		}
	}
	
	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.side == Side.SERVER) {
			World world = event.world;
			if (world != null) {
				VillageCollection villages = world.getVillageCollection();
				for (Village village : villages.getVillageList()) {
					if (world.isBlockLoaded(village.getCenter())) {
						if (village.hasCapability(RaidsContent.RAID_CAPABILITY, null)) {
							IRaid raid = village.getCapability(RaidsContent.RAID_CAPABILITY, null);
							if (raid.isActive(world)) raid.update(world);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			EntityPlayer player = event.player;
			if (player!=null) {
				if (player.isPotionActive(RaidsContent.BAD_OMEN)) {
					World world = player.world;
					if (world.getDifficulty() != EnumDifficulty.PEACEFUL) {
						Village village = world.getVillageCollection().getNearestVillage(player.getPosition(), 10);
						if (village!=null) {
							if (village.hasCapability(RaidsContent.RAID_CAPABILITY, null)) {
								IRaid raid = village.getCapability(RaidsContent.RAID_CAPABILITY, null);
								if (!raid.isActive(world)) {
									int amplifier = player.getActivePotionEffect(RaidsContent.BAD_OMEN).getAmplifier();
									int waves = RaidHandler.getWaveCount(world);
									if (amplifier > 0) waves++;
									if (waves>0) {
										raid.startEvent(waves, amplifier > 0 ? 1 : 0, amplifier);
										player.removeActivePotionEffect(RaidsContent.BAD_OMEN);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onDamage(LivingDamageEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity!=null) {
			World world = entity.world;
			if (!world.isRemote && entity instanceof EntityLiving) {
				if (entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null)) {
					IRaider raider = entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null);
					if (raider.hasRaid()) {
						IRaid raid = raider.getRaid();
						if (raid.isActive(world)) raid.takeDamage((EntityLiving) entity, event.getSource(), event.getAmount());
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity!=null) {
			World world = entity.world;
			if (!world.isRemote && entity instanceof EntityLiving) {
				if (entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null)) {
					IRaider raider = entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null);
					if (raider.hasRaid()) {
						IRaid raid = raider.getRaid();
						if (raid.isActive(world)) raid.entityDie((EntityLiving) entity);
					} else if (raider.isLeader()) {
						DamageSource source = event.getSource();
						Entity attacker = source.getTrueSource() == null ? source.getImmediateSource() == null ? null : source.getImmediateSource() : source.getTrueSource();
						if (attacker!=null) {
							if (attacker instanceof IEntityOwnable) attacker = ((IEntityOwnable) attacker).getOwner();
							if (attacker instanceof EntityPlayer) {
								EntityPlayer player = (EntityPlayer) entity;
								if (player.isPotionActive(RaidsContent.BAD_OMEN)) {
									int amplifier = player.getActivePotionEffect(RaidsContent.BAD_OMEN).getAmplifier();
									if (amplifier < 4) player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 120000, amplifier+1));
									player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 120000));
								} else player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 120000));
								//if (player instanceof EntityPlayerMP) ((EntityPlayerMP) player).getAdvancements().grantCriterion(Advancement, p_192750_2_)
							}
						}
					}
				}
			}
		}
	}
}
