package net.smileycorp.raids.common.raid;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.*;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsLogger;
import net.smileycorp.raids.common.entities.ai.EntityAIPathfindToRaid;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidSoundMessage;
import net.smileycorp.raids.config.RaidConfig;

import javax.annotation.Nullable;
import java.util.*;

public class Raid {
	
	private static final ITextComponent RAID_NAME_COMPONENT = new TextComponentTranslation("event.raids.raid");
	private static final ITextComponent VICTORY = new TextComponentTranslation("event.raids.raid.victory");
	private static final ITextComponent DEFEAT = new TextComponentTranslation("event.raids.raid.defeat");
	private static final ITextComponent RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.createCopy()
			.appendSibling(new TextComponentString(" - ")).appendSibling(VICTORY);
	private static final ITextComponent RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.createCopy()
			.appendSibling(new TextComponentString(" - ")).appendSibling(DEFEAT);
	private final Map<Integer, EntityLiving> groupToLeaderMap = Maps.newHashMap();
	private final Map<Integer, Set<EntityLiving>> groupEntityLivingMap = Maps.newHashMap();
	private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
	private long ticksActive;
	private BlockPos center;
	private WorldServer world;
	private boolean started;
	private final int id;
	private float totalHealth;
	private int badOmenLevel;
	private boolean active;
	private int groupsSpawned;
	private final BossInfoServer raidEvent = new BossInfoServer(RAID_NAME_COMPONENT, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
	private int postRaidTicks;
	private int raidCooldownTicks;
	private final Random random = new Random();
	private final int numGroups;
	private Status status;
	private int celebrationTicks;
	private Optional<BlockPos> waveSpawnPos = Optional.empty();
	
	public Raid(int id, WorldServer world, BlockPos center) {
		this.id = id;
		this.world = world;
		active = true;
		this.center = center;
		raidCooldownTicks = 300;
		raidEvent.setPercent(0.0F);
		numGroups = getNumGroups(world.getDifficulty());
		status = Status.ONGOING;
	}
	
	public Raid(WorldServer world, NBTTagCompound nbt) {
		this.world = world;
		id = nbt.getInteger("Id");
		started = nbt.getBoolean("Started");
		active = nbt.getBoolean("Active");
		ticksActive = nbt.getLong("TicksActive");
		badOmenLevel = nbt.getInteger("BadOmenLevel");
		groupsSpawned = nbt.getInteger("GroupsSpawned");
		raidCooldownTicks = nbt.getInteger("PreRaidTicks");
		postRaidTicks = nbt.getInteger("PostRaidTicks");
		totalHealth = nbt.getFloat("TotalHealth");
		center = new BlockPos(nbt.getInteger("CX"), nbt.getInteger("CY"), nbt.getInteger("CZ"));
		numGroups = nbt.getInteger("NumGroups");
		status = Status.getByName(nbt.getString("Status"));
		heroesOfTheVillage.clear();
		if (nbt.hasKey("HeroesOfTheVillage", 9)) {
			NBTTagList list = nbt.getTagList("HeroesOfTheVillage", 10);
			for(NBTBase nbtBase : list) heroesOfTheVillage.add(NBTUtil.getUUIDFromTag((NBTTagCompound) nbtBase));
		}
	}
	
	public boolean isOver() {
		return isVictory() || isLoss();
	}
	
	public boolean hasFirstWaveSpawned() {
		return groupsSpawned > 0;
	}
	
	public boolean isStopped() {
		return status == Status.STOPPED;
	}
	
	public boolean isVictory() {
		return status == Status.VICTORY;
	}
	
	public boolean isLoss() {
		return status == Status.LOSS;
	}
	
	public Set<EntityLiving> getAllEntityLivings() {
		Set<EntityLiving> set = Sets.newHashSet();
		for(Set<EntityLiving> set1 : groupEntityLivingMap.values()) set.addAll(set1);
		return set;
	}
	
	public WorldServer getWorld() {
		return world;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public int getGroupsSpawned() {
		return groupsSpawned;
	}
	
	public int getNumGroups() {
		return numGroups;
	}
	
	private Predicate<EntityPlayerMP> validPlayer() {
		return (player) -> {
			BlockPos blockpos = player.getPosition();
			return player.isEntityAlive() && WorldDataRaids.getData(world).getRaidAt(blockpos) == this;
		};
	}
	
	private void updatePlayers() {
		Set<EntityPlayerMP> set = Sets.newHashSet(raidEvent.getPlayers());
		List<EntityPlayerMP> list = world.getPlayers(EntityPlayerMP.class, validPlayer());
		for(EntityPlayerMP EntityPlayerMP : list) if (!set.contains(EntityPlayerMP)) raidEvent.addPlayer(EntityPlayerMP);
		for(EntityPlayerMP EntityPlayerMP1 : set) if (!list.contains(EntityPlayerMP1)) raidEvent.removePlayer(EntityPlayerMP1);
	}
	
	public int getMaxBadOmenLevel() {
		return 5;
	}
	
	public int getBadOmenLevel() {
		return badOmenLevel;
	}
	
	public void setBadOmenLevel(int level) {
		badOmenLevel = level;
	}
	
	public void absorbBadOmen(EntityPlayerMP player) {
		if ((player.isPotionActive(RaidsContent.BAD_OMEN) &! RaidConfig.ominousBottles) || (player.isPotionActive(RaidsContent.RAID_OMEN) && RaidConfig.ominousBottles)) {
			badOmenLevel += player.getActivePotionEffect(RaidConfig.ominousBottles ? RaidsContent.RAID_OMEN : RaidsContent.BAD_OMEN).getAmplifier() + 1;
			badOmenLevel = MathUtils.clamp(badOmenLevel, 0, getMaxBadOmenLevel());
		}
		player.removePotionEffect(RaidConfig.ominousBottles ? RaidsContent.RAID_OMEN : RaidsContent.BAD_OMEN);
	}
	
	public void stop() {
		active = false;
		Set<EntityPlayerMP> players = Sets.newHashSet();
		raidEvent.getPlayers().stream().forEach(players::add);
		players.stream().forEach(raidEvent::removePlayer);
		status = Status.STOPPED;
	}
	
	public void tick() {
		if (!isStopped()) {
			if (status == Status.ONGOING) {
				boolean flag = active;
				active = world.isBlockLoaded(center);
				if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
					stop();
					return;
				}
				if (flag != active) raidEvent.setVisible(active);
				if (!active) return;
				if (!isVillage(world, center)) {
					moveRaidCenterToNearbyVillageSection();
				}
				
				if (!isVillage(world, center)) {
					if (groupsSpawned > 0) {
						status = Status.LOSS;
					} else stop();
				}
				if (ticksActive++ >= 48000) {
					stop();
					return;
				}
				int i = getTotalEntityLivingsAlive();
				if (i == 0 && hasMoreWaves()) {
					if (raidCooldownTicks <= 0) {
						if (raidCooldownTicks == 0 && groupsSpawned > 0) {
							raidCooldownTicks = 300;
							raidEvent.setName(RAID_NAME_COMPONENT);
							return;
						}
					} else {
						boolean flag1 = waveSpawnPos.isPresent();
						boolean flag2 = !flag1 && raidCooldownTicks % 5 == 0;
						if (flag1 && !world.isBlockLoaded(waveSpawnPos.get())) flag2 = true;
						if (flag2) {
							int j = 0;
							if (raidCooldownTicks < 100) {
								j = 1;
							} else if (raidCooldownTicks < 40) {
								j = 2;
							}
							waveSpawnPos = getValidSpawnPos(j);
						}
						if (raidCooldownTicks == 300 || raidCooldownTicks % 20 == 0) updatePlayers();
						raidCooldownTicks--;
					}
				}
				if (ticksActive % 20 == 0) {
					updatePlayers();
					updateEntityLivings();
					if (i > 0) {
						if (i <= 2) raidEvent.setName(RAID_NAME_COMPONENT.createCopy().appendSibling(new TextComponentString(" - "))
								.appendSibling(new TextComponentTranslation("event.raids.raid.raiders_remaining", i)));
						else raidEvent.setName(RAID_NAME_COMPONENT);
					} else raidEvent.setName(RAID_NAME_COMPONENT);
				}
				boolean flag3 = false;
				int attempt = 0;
				while(shouldSpawnGroup()) {
					BlockPos blockpos = waveSpawnPos.isPresent() ? waveSpawnPos.get() : findRandomSpawnPos(attempt, 20);
					if (blockpos != null) {
						started = true;
						totalHealth = 0;
						RaidHandler.spawnNewWave(this, blockpos, groupsSpawned + 1, shouldSpawnBonusGroup());
						waveSpawnPos = Optional.empty();
						groupsSpawned++;
						updateBossbar();
						setDirty();
						if (!flag3) {
							playSound(blockpos);
							flag3 = true;
						}
					} else attempt++;
					if (attempt > 3) {
						stop();
						break;
					}
				}
				if (isStarted() && !hasMoreWaves() && i == 0) {
					if (postRaidTicks < 40) postRaidTicks++;
					else {
						status = Status.VICTORY;
						for(UUID uuid : heroesOfTheVillage) {
							Entity entity = world.getEntityFromUuid(uuid);
							if (entity instanceof EntityLivingBase) {
								EntityLivingBase living = (EntityLivingBase)entity;
								living.addPotionEffect(new PotionEffect(RaidsContent.HERO_OF_THE_VILLAGE, 48000, badOmenLevel - 1, false, true));
							}
							if (entity instanceof EntityPlayerMP) RaidsContent.RAID_VICTORY.trigger((EntityPlayerMP) entity);
						}
					}
				}
				updateBossbar();
				setDirty();
			} else if (isOver()) {
				if (celebrationTicks++ >= 600) {
					stop();
					return;
				}
				if (celebrationTicks % 20 == 0) {
					updatePlayers();
					raidEvent.setVisible(true);
					if (isVictory()) {
						raidEvent.setPercent(0);
						raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
					} else {
						raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
					}
				}
			}
		}
	}
	
	public static boolean isVillage(World world, BlockPos center) {
		return isVillage(world, center, 0);
	}
	
	public static boolean isVillage(World world, BlockPos center, double distance) {
		for (Village village : world.getVillageCollection().getVillageList()){
			BlockPos vilCenter = village.getCenter();
			if (vilCenter.getDistance(center.getX(), center.getY(), center.getZ()) < (village.getVillageRadius() + distance * distance)) return true;
		}
		return false;
	}
	
	private void moveRaidCenterToNearbyVillageSection() {
		for (Village village : world.getVillageCollection().getVillageList()) {
			BlockPos vilCenter = village.getCenter();
			if (vilCenter.getDistance(center.getX(), center.getY(), center.getZ()) < 72)
				setCenter(village.getCenter());
		}
	}
	
	private Optional<BlockPos> getValidSpawnPos(int p_37764_) {
		for(int i = 0; i < 3; i++) {
			BlockPos blockpos = findRandomSpawnPos(p_37764_, 1);
			if (blockpos != null) return Optional.of(blockpos);
		}
		return Optional.empty();
	}
	
	private boolean hasMoreWaves() {
		return !(hasBonusWave() ? hasSpawnedBonusWave() : isFinalWave());
	}
	
	private boolean isFinalWave() {
		return getGroupsSpawned() == numGroups;
	}
	
	private boolean hasBonusWave() {
		return badOmenLevel > 1;
	}
	
	private boolean hasSpawnedBonusWave() {
		return getGroupsSpawned() > numGroups;
	}
	
	private boolean shouldSpawnBonusGroup() {
		return isFinalWave() && getTotalEntityLivingsAlive() == 0 && hasBonusWave();
	}
	
	private void updateEntityLivings() {
		Iterator<Set<EntityLiving>> iterator = groupEntityLivingMap.values().iterator();
		Set<EntityLiving> set = Sets.newHashSet();
		while(iterator.hasNext()) {
			Set<EntityLiving> set1 = iterator.next();
			for(EntityLiving entity : set1) {
				BlockPos blockpos = entity.getPosition();
				if (entity.isEntityAlive() && entity.isAddedToWorld() && entity.hasCapability(RaidsContent.RAIDER, null)
						&& entity.world.provider.getDimension() == world.provider.getDimension() && !(center.distanceSq(blockpos) >= 12544)) {
					Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
					if (entity.ticksExisted > 600) {
						if (world.getEntityFromUuid(entity.getUniqueID()) == null) set.add(entity);
						if (!isVillage(world, blockpos) && entity.getIdleTime() > 2400) raider.setTicksOutsideRaid(raider.getTicksOutsideRaid() + 1);
						if (entity.hasCapability(RaidsContent.RAIDER, null) && raider.getTicksOutsideRaid() >= 30) set.add(entity);
					}
				} else set.add(entity);
			}
		}
		for (EntityLiving EntityLiving1 : set) removeFromRaid(EntityLiving1, true);
	}
	
	private void playSound(BlockPos pos) {
		float f = 13.0F;
		int i = 64;
		Collection<EntityPlayerMP> collection = raidEvent.getPlayers();
		for(EntityPlayer player : world.playerEntities) {
			Vec3d vec3 = player.getPositionVector();
			Vec3d vec31 = new Vec3d(pos);
			double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z));
			double d1 = vec3.x + 13 / d0 * (vec31.x - vec3.x);
			double d2 = vec3.z + 13 / d0 * (vec31.z - vec3.z);
			if ((d0 <= 64 || collection.contains(player)) && player instanceof EntityPlayerMP)
				PacketHandler.NETWORK_INSTANCE.sendTo(new RaidSoundMessage(new BlockPos(d0, d1, d2)), (EntityPlayerMP)player);
		}
	}
	
	
	public void joinRaid(int wave, EntityLiving entity, boolean recruited) {
		if (addWaveMob(wave, entity)) {
			Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
			raider.setCurrentRaid(this);
			raider.setWave(wave);
			raider.setTicksOutsideRaid(0);
			if (entity instanceof EntityCreature) {
				entity.tasks.addTask(3, new EntityAIPathfindToRaid(raider, (EntityCreature) entity));
				entity.tasks.addTask(4, new EntityAIMoveThroughVillage((EntityCreature) entity, 1, false));
			}
			if (!recruited) RaidHandler.applyRaidBuffs(entity, this, wave, random);
		}
	}
	
	public void updateBossbar() {
		if (getTotalEntityLivingsAlive() <= 0 && hasMoreWaves() && raidCooldownTicks > 0) {
			raidEvent.setPercent(MathUtils.clamp((float)(300 - raidCooldownTicks) / 300, 0, 1));
			return;
		}
		Set toRemove = Sets.newHashSet();
		for (Set<EntityLiving> set : groupEntityLivingMap.values()) {
			for (EntityLiving entity : set) {
				if (!entity.isEntityAlive()) {
					toRemove.add(entity);
					totalHealth -= entity.getHealth();
				}
			}
			set.removeAll(toRemove);
		}
		raidEvent.setPercent(MathUtils.clamp(getHealthOfEntities() / totalHealth, 0, 1));
	}
	
	public float getHealthOfEntities() {
		float health = 0;
		for (Set<EntityLiving> set : groupEntityLivingMap.values()) for (EntityLiving entity : set) health += entity.getHealth();
		return health;
	}
	
	private boolean shouldSpawnGroup() {
		return raidCooldownTicks == 0 && (groupsSpawned < numGroups || shouldSpawnBonusGroup()) && getTotalEntityLivingsAlive() == 0;
	}
	
	public int getTotalEntityLivingsAlive() {
		return getAllEntityLivings().size();
	}
	
	public void removeFromRaid(EntityLiving entity, boolean update) {
		if (!entity.hasCapability(RaidsContent.RAIDER, null)) return;
		Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
		Set<EntityLiving> set = groupEntityLivingMap.get(raider.getWave());
		if (set != null) {
			boolean flag = set.remove(entity);
			if (flag) {
				if (update) totalHealth -= entity.getHealth();
				raider.setCurrentRaid(null);
				updateBossbar();
				setDirty();
			}
		}
	}
	
	private void setDirty() {
		WorldDataRaids.getData(world).setDirty(true);
	}
	
	@Nullable
	private BlockPos findRandomSpawnPos(int attempt, int tries) {
		int i = attempt == 0 ? 2 : 2 - attempt;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for(int i1 = 0; i1 < tries; ++i1) {
			float f = random.nextFloat() * ((float)Math.PI * 2);
			int x = center.getX() + (int)Math.floor(Math.cos(f) * 32 * (float)i) + random.nextInt(5);
			int z = center.getZ() + (int)Math.floor(Math.sin(f) * 32 * (float)i) + random.nextInt(5);
			int y = world.getHeight(x, z);
			pos.setPos(x, y, z);
			if ((isVillage(world, pos) || attempt >= 2) && world.isAreaLoaded(new StructureBoundingBox(pos.getX() - 10, pos.getZ() - 10,
						pos.getX() + 10, pos.getZ() + 10))
						&& world.isAirBlock(pos)) return pos;
		}
		return null;
	}
	
	private boolean addWaveMob(int index, EntityLiving entity) {
		return addWaveMob(index, entity, true);
	}
	
	public boolean addWaveMob(int index, EntityLiving entity, boolean addHealth) {
		RaidsLogger.logInfo("Adding entity " + entity + ", " + entity.hasCapability(RaidsContent.RAIDER, null) + ", " + entity.isAddedToWorld()+ ", " + entity.isEntityAlive() + ", " + addHealth);
		if (!entity.hasCapability(RaidsContent.RAIDER, null)) return false;
		groupEntityLivingMap.computeIfAbsent(index, v -> Sets.newHashSet());
		Set<EntityLiving> set = groupEntityLivingMap.get(index);
		EntityLiving entity0 = null;
		for(EntityLiving entity1 : set) if (entity1.getUniqueID().equals(entity.getUniqueID())) {
			entity0 = entity1;
			break;
		}
		if (entity0 != null) {
			set.remove(entity0);
			set.add(entity);
		}
		set.add(entity);
		if (addHealth) totalHealth += entity.getHealth();
		updateBossbar();
		setDirty();
		return true;
	}
	
	public void setLeader(int index, EntityLiving entity) {
		groupToLeaderMap.put(index, entity);
		entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, RaidsContent.createOminousBanner());
		entity.setDropChance(EntityEquipmentSlot.HEAD, 2.0F);
	}
	
	public void removeLeader(int index) {
		groupToLeaderMap.remove(index);
	}
	
	public BlockPos getCenter() {
		return center;
	}
	
	public void setCenter(BlockPos center) {
		this.center = center;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public NBTTagCompound save(NBTTagCompound nbt) {
		nbt.setInteger("Id", id);
		nbt.setBoolean("Started", started);
		nbt.setBoolean("Active", active);
		nbt.setLong("TicksActive", ticksActive);
		nbt.setInteger("BadOmenLevel", badOmenLevel);
		nbt.setInteger("GroupsSpawned", groupsSpawned);
		nbt.setInteger("PreRaidTicks", raidCooldownTicks);
		nbt.setInteger("PostRaidTicks", postRaidTicks);
		nbt.setFloat("TotalHealth", totalHealth);
		nbt.setInteger("NumGroups", numGroups);
		nbt.setString("Status", status.getName());
		nbt.setInteger("CX", center.getX());
		nbt.setInteger("CY", center.getY());
		nbt.setInteger("CZ", center.getZ());
		NBTTagList listtag = new NBTTagList();
		for(UUID uuid : heroesOfTheVillage) listtag.appendTag(NBTUtil.createUUIDTag(uuid));
		nbt.setTag("HeroesOfTheVillage", listtag);
		return nbt;
	}
	
	public int getNumGroups(EnumDifficulty difficulty) {
		switch(difficulty) {
			case EASY:
				return 3;
			case NORMAL:
				return 5;
			case HARD:
				return 7;
			default:
				return 0;
		}
	}
	
	public float getEnchantOdds() {
		int i = getBadOmenLevel();
		if (i == 2) {
			return 0.1F;
		} else if (i == 3) {
			return 0.25F;
		} else if (i == 4) {
			return 0.5F;
		} else {
			return i == 5 ? 0.75F : 0.0F;
		}
	}
	
	public void addHeroOfTheVillage(Entity entity) {
		heroesOfTheVillage.add(entity.getUniqueID());
	}
	
	public List<String> getEntityStrings() {
		List<String> result = new ArrayList<>();
		result.add("totalHealth=" + totalHealth + ", currentHealth="+ getHealthOfEntities());
		result.add("	entities: {");
		List<EntityLiving> entitylist = new ArrayList<>();
		for (Set<EntityLiving> set : groupEntityLivingMap.values()) for (EntityLiving entity : set) entitylist.add(entity);
		for (int i = 0; i < entitylist.size(); i += 10) {
			List<EntityLiving> sublist = entitylist.subList(i, Math.min(i+9, entitylist.size()-1));
			StringBuilder builder = new StringBuilder();
			builder.append("		");
			for (EntityLiving entity : sublist) {
				builder.append(entity.getClass().getSimpleName() + "@");
				builder.append(Integer.toHexString(entity.hashCode()));
				builder.append(entity.getPosition());
				if (entitylist.indexOf(entity) < entitylist.size() - 1) builder.append(", ");
			}
			builder.append("}");
			result.add(builder.toString());
		}
		return result;
	}
	
	public void setWorld(WorldServer world) {
		this.world = world;
	}
    
    enum Status {
		ONGOING,
		VICTORY,
		LOSS,
		STOPPED;
		
		private static final Status[] VALUES = values();
		
		static Status getByName(String p_37804_) {
			for(Status raid$raidstatus : VALUES) {
				if (p_37804_.equalsIgnoreCase(raid$raidstatus.name())) {
					return raid$raidstatus;
				}
			}
			
			return ONGOING;
		}
		
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
	
}