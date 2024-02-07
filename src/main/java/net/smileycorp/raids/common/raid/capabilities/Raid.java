package net.smileycorp.raids.common.raid.capabilities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.atlas.api.IOngoingEvent;
import net.smileycorp.atlas.api.util.DataUtils;
import net.smileycorp.raids.common.Raids;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public interface Raid extends IOngoingEvent {

	Village getVillage();

	float getWaveHealth();

	float getTotalWaveHealth();

	List<EntityLiving> getWaveEntities();

	void startEvent(int maxWaves, int bonusWaves, int level);

	void startNextWave();

	void endEvent();

	void takeDamage(EntityLiving entity, DamageSource source, float damage);

	void entityDie(EntityLiving entity);

	int entitiesRemaining();

	class Storage implements IStorage<Raid> {

		@Override
		public NBTBase writeNBT(Capability<Raid> capability, Raid instance, EnumFacing side) {
			return instance.writeToNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<Raid> capability, Raid instance, EnumFacing side, NBTBase nbt) {
			instance.readFromNBT((NBTTagCompound) nbt);
		}

	}

	class Provider implements ICapabilitySerializable<NBTTagCompound> {

		protected Raid impl;

		public Provider(Village village) {
			impl = new Impl(village);
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

	class Impl implements Raid {

		protected final List<EntityLiving> entities = new ArrayList<>();
		protected final List<EntityPlayer> participants = new ArrayList<>();
		protected final List<EntityVillager> villagers = new ArrayList<>();
		private final BossInfoServer bossInfo;
		protected int cooldown = 0;
		protected boolean fireworks = false;
		protected int wave = 0, maxWaves = 0, bonusWaves = 0, level = 0;
		protected float health = 0, totalHealth = 0;
		protected final Random rand = new Random();
		protected boolean isActive;
		protected Village village = null;
		protected World world = null;

		public Impl() {
			bossInfo = new BossInfoServer(new TextComponentTranslation("event.raids.raid"), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
			bossInfo.setVisible(false);
		}

		public Impl(Village village) {
			this();
			this.village = village;
			world = village.world;
			for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, player -> player.getDistanceSqToCenter(village.getCenter()) <= 9216))
				bossInfo.addPlayer(player);
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("cooldown")) cooldown = nbt.getInteger("cooldown");
			if (nbt.hasKey("fireworks")) fireworks = nbt.getBoolean("fireworks");
			if (nbt.hasKey("wave")) wave = nbt.getInteger("wave");
			if (nbt.hasKey("isActive")) isActive = nbt.getBoolean("isActive");
			if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
				World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
				if (nbt.hasKey("entities")) {
					for (NBTBase tag : nbt.getTagList("entities", 3)) {
						entities.add((EntityLiving) world.getEntityByID(((NBTTagInt) tag).getInt()));
					}
				}
				if (nbt.hasKey("participants") && !world.isRemote) {
					for (NBTBase tag : nbt.getTagList("participants", 8)) {
						String uuid = ((NBTTagString)tag).getString();
						if (DataUtils.isValidUUID(uuid)) participants.add(world.getPlayerEntityByUUID(UUID.fromString(uuid)));
					}
				}
			}
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("cooldown", cooldown);
			nbt.setBoolean("fireworks", fireworks);
			nbt.setInteger("wave", wave);
			nbt.setBoolean("isActive", isActive);
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
				Raids.logInfo("tick cooldown");
				if (fireworks && world != null) {
					for (EntityVillager villager : villagers) {
						if (rand.nextInt(200) == 0 && world.canSeeSky(villager.getPos())) {
							EntityFireworkRocket firework = new EntityFireworkRocket(world, villager.posX, villager.posY, villager.posZ, RaidsContent.getVillagerFirework(rand));
							world.spawnEntity(firework);
						}
					}
					if (cooldown == 0) {
						isActive = false;
						fireworks = false;
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
			return isActive ;
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
			bossInfo.setVisible(true);
			cooldown = 60;
			isActive = true;
			this.maxWaves = maxWaves;
			this.level = level;
		}

		@Override
		public void startNextWave() {
			wave++;
			boolean isBonusWave = wave > maxWaves;
			entities.addAll(RaidHandler.createNewWave(village, isBonusWave ? maxWaves : wave, level , isBonusWave));
			health = 0;
			for (EntityLiving entity : entities) health += entity.getHealth();
			totalHealth = health;
		}

		@Override
		public void endEvent() {
			bossInfo.setVisible(false);
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
			health -= damage;
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

		@Override
		public Village getVillage() {
			return village;
		}
		
		@Override
		public String toString() {
			return "Raid@" + Integer.toHexString(hashCode()) + "[village=" + (village == null ? "null" : village.getCenter()) + ", isActive=" + isActive +
					", cooldown=" + cooldown + ", wave=" + wave + ", maxWaves=" + maxWaves + ", bonusWaves=" + bonusWaves +  ", level=" + level +
					", entityCount="+ entities.size()+", villagers="+villagers + ", participants="+participants+"]";
		}
		
	}
}
