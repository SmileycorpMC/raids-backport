package net.smileycorp.raids.common.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.atlas.api.IOngoingEvent;
import net.smileycorp.atlas.api.util.DataUtils;
import net.smileycorp.raids.common.RaidHandler;
import net.smileycorp.raids.common.RaidsContent;

public interface IRaid extends IOngoingEvent {

	public Village getVillage();
	
	public float getWaveHealth();
	
	public float getTotalWaveHealth();
	
	public List<EntityLiving> getWaveEntities();
	
	public void startEvent(int maxWaves, int bonusWaves, int level);
	
	public void startNextWave();
	
	public void endEvent();
	
	public void takeDamage(EntityLiving entity, DamageSource source, float damage);
	
	public void entityDie(EntityLiving entity);
	
	public int entitiesRemaining();
	
	public class Storage implements IStorage<IRaid> {

		@Override
		public NBTBase writeNBT(Capability<IRaid> capability, IRaid instance, EnumFacing side) {
			return instance.writeToNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<IRaid> capability, IRaid instance, EnumFacing side, NBTBase nbt) {
			instance.readFromNBT((NBTTagCompound) nbt);
		}
		
	}
	
	public static class Implementation implements IRaid {
		
		protected final World world;
		protected final Village village;
		protected final List<EntityLiving> entities = new ArrayList<EntityLiving>();
		protected final List<EntityPlayer> participants = new ArrayList<EntityPlayer>();
		protected final List<EntityVillager> villagers = new ArrayList<EntityVillager>();
		protected int cooldown = 0;
		protected boolean fireworks = false;
		protected int wave = 0, maxWaves = 0, bonusWaves = 0, level = 0;
		protected float health = 0, totalHealth = 0;
		protected final Random rand = new Random();
		
		public Implementation() {
			this.village = null;
			this.world = null;
		}

		public Implementation(Village village) {
			this.village = village;
			this.world = village.world;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("cooldown")) cooldown = nbt.getInteger("cooldown");
			if (nbt.hasKey("fireworks")) fireworks = nbt.getBoolean("fireworks");
			if (nbt.hasKey("wave")) wave = nbt.getInteger("wave");
			if (world!=null) {
				if (nbt.hasKey("entities")) {
					for (NBTBase tag : nbt.getTagList("entities", 3)) {
						entities.add((EntityLiving) world.getEntityByID(((NBTTagInt) tag).getInt()));
					}
				}
				if (nbt.hasKey("participants") && !world.isRemote) {
					for (NBTBase tag : nbt.getTagList("participants", 8)) {
						String uuid = ((NBTTagString)tag).getString();
						if (DataUtils.isValidUUID(uuid)) participants.add(((WorldServer) world).getPlayerEntityByUUID(UUID.fromString(uuid)));
					}
				}
			}
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("cooldown", cooldown);
			nbt.setBoolean("fireworks", fireworks);
			nbt.setInteger("wave", wave);
			NBTTagList entitiesList = new NBTTagList();
			for (EntityLiving entity : entities) entitiesList.appendTag(new NBTTagInt(entity.getEntityId()));
			nbt.setTag("entities", entitiesList);
			NBTTagList participantsList = new NBTTagList();
			for (EntityPlayer player : participants) participantsList.appendTag(new NBTTagString(EntityPlayer.getUUID(player.getGameProfile()).toString()));
			nbt.setTag("participants", participantsList);
			return nbt;
		}

		@Override
		public void update(World world) {
			if (cooldown > 0) {
				cooldown--;
				if (fireworks && world != null) {
					for (EntityVillager villager : villagers) {
						if (rand.nextInt(200) == 0 && world.canSeeSky(villager.getPos())) {
							EntityFireworkRocket firework = new EntityFireworkRocket(world, villager.posX, villager.posY, villager.posZ, RaidsContent.getVillagerFirework(rand));
							world.spawnEntity(firework);
						}
					}
				}
				if (cooldown == 0) {
					if (!fireworks) {
						startNextWave();
					} else  {
						fireworks = false;
						villagers.clear();
					}
				}
			}
		}

		@Override
		public boolean isActive(World world) {
			return world.isRemote && wave > 0 ;
		}

		@Override
		public Village getVillage() {
			return village;
		}

		@Override
		public float getWaveHealth() {
			return health;
		}

		@Override
		public float getTotalWaveHealth() {
			return totalHealth;
		}

		@Override
		public List<EntityLiving> getWaveEntities() {
			List<EntityLiving> result = new ArrayList<EntityLiving>();
			for (EntityLiving entity : entities) if (!entity.isDead) result.add(entity);
			return result;
		}
		
		@Override
		public void startEvent(int maxWaves, int bonusWaves, int level) {
			cooldown = 60;
			this.maxWaves = maxWaves;
			this.level = level;
		}

		@Override
		public void startNextWave() {
			wave++;
			boolean isBonusWave = wave > maxWaves;
			entities.addAll(RaidHandler.createNewWave(village, isBonusWave ? maxWaves : wave, level , isBonusWave));
			health = 0;
			for (EntityLiving entity : entities) health+=entity.getHealth();
			totalHealth = health;
		}
		
		@Override
		public void endEvent() {
			fireworks = true;
			for (EntityPlayer player : participants)  {
				player.addPotionEffect(new PotionEffect(RaidsContent.HERO_OF_THE_VILLAGE, 48000, level));
				village.modifyPlayerReputation(EntityPlayer.getUUID(player.getGameProfile()), 10);
			}
			if (world != null) {
				villagers.addAll(world.<EntityVillager>getEntitiesWithinAABB(EntityVillager.class, 
						new AxisAlignedBB(village.getCenter().getX() - village.getVillageRadius(), village.getCenter().getY() - 4, village.getCenter().getZ() - village.getVillageRadius(), 
								village.getCenter().getX() + village.getVillageRadius(), village.getCenter().getY() + 4, village.getCenter().getZ() + village.getVillageRadius())));
			}
			wave = 0;
			maxWaves = 0;
			level = 0;
			health = 0;
			totalHealth = 0;
			participants.clear();
			entities.clear();
			cooldown = 60;
		}	

		@Override
		public void takeDamage(EntityLiving entity, DamageSource source, float damage) {
			totalHealth -= damage;
			if (source != null) {
				if (source.getImmediateSource() instanceof EntityPlayer) participants.add((EntityPlayer) source.getImmediateSource());
				else if (source.getTrueSource() instanceof EntityPlayer) participants.add((EntityPlayer) source.getTrueSource());
			}
		}

		@Override
		public void entityDie(EntityLiving entity) {
			entities.remove(entity);
			if (entities.isEmpty()) {
				if (wave == maxWaves) {
					endEvent();
				} else cooldown = 60;
			}
		}

		@Override
		public int entitiesRemaining() {
			return entities.size();
		}
		
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		
		protected IRaid impl;
		
		public Provider(Village village) {
			impl = new Implementation(village);
		}

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == RaidsContent.RAID_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? RaidsContent.RAID_CAPABILITY.cast(impl) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return impl.writeToNBT(new NBTTagCompound());
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			impl.readFromNBT(nbt);
		}
		
	}
}
