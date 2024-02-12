package net.smileycorp.raids.common.raid;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidSoundMessage;

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
		badOmenLevel = nbt.getInteger("BadOmenworld");
		groupsSpawned = nbt.getInteger("GroupsSpawned");
		raidCooldownTicks = nbt.getInteger("PreRaidTicks");
		postRaidTicks = nbt.getInteger("PostRaidTicks");
		totalHealth = nbt.getFloat("TotalHealth");
		center = new BlockPos(nbt.getInteger("CX"), nbt.getInteger("CY"), nbt.getInteger("CZ"));
		numGroups = nbt.getInteger("NumGroups");
		status = Status.getByName(nbt.getString("Status"));
		heroesOfTheVillage.clear();
		if (nbt.hasKey("HeroesOfTheVillage", 9)) {
			NBTTagList listtag = nbt.getTagList("HeroesOfTheVillage", 10);
			for(NBTBase nbtBase : listtag) {
				NBTTagCompound compound = (NBTTagCompound) nbtBase;
				heroesOfTheVillage.add(new UUID(compound.getLong("Most"), compound.getLong("Least")));
			}
		}
	}
	
	public boolean isOver() {
		return isVictory() || isLoss();
	}
	
	public boolean isBetweenWaves() {
		return hasFirstWaveSpawned() && getTotalEntityLivingsAlive() == 0 && raidCooldownTicks > 0;
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
	
	public float getTotalHealth() {
		return totalHealth;
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
	
	public int getMaxBadOmenworld() {
		return 5;
	}
	
	public int getBadOmenLevel() {
		return badOmenLevel;
	}
	
	public void setBadOmenLevel(int level) {
		badOmenLevel = level;
	}
	
	public void absorbBadOmen(EntityPlayerMP player) {
		if (player.isPotionActive(RaidsContent.BAD_OMEN)) {
			badOmenLevel += player.getActivePotionEffect(RaidsContent.BAD_OMEN).getAmplifier() + 1;
			badOmenLevel = MathUtils.clamp(badOmenLevel, 0, getMaxBadOmenworld());
		}
		player.removePotionEffect(RaidsContent.BAD_OMEN);
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
				if (ticksActive++ >= 48000L) {
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
						raidEvent.setPercent(MathUtils.clamp((float)(300 - raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
					}
				}
				if (ticksActive % 20L == 0L) {
					updatePlayers();
					updateEntityLivings();
					if (i > 0) {
						if (i <= 2) raidEvent.setName(RAID_NAME_COMPONENT.createCopy().appendSibling(new TextComponentString(" - "))
								.appendSibling(new TextComponentTranslation("event.raids.raid.raiders_remaining", i)));
						else raidEvent.setName(RAID_NAME_COMPONENT);
					} else raidEvent.setName(RAID_NAME_COMPONENT);
				}
				boolean flag3 = false;
				int k = 0;
				while(shouldSpawnGroup()) {
					BlockPos blockpos = waveSpawnPos.isPresent() ? waveSpawnPos.get() : findRandomSpawnPos(k, 20);
					if (blockpos != null) {
						started = true;
						totalHealth = 0;
						RaidHandler.spawnNewWave(this, blockpos, groupsSpawned++, shouldSpawnBonusGroup());
						updateBossbar();
						setDirty();
						if (!flag3) {
							playSound(blockpos);
							flag3 = true;
						}
					} else k++;
					if (k > 3) {
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
							if (entity instanceof EntityLiving) {
								EntityLiving EntityLiving = (EntityLiving)entity;
								EntityLiving.addPotionEffect(new PotionEffect(RaidsContent.HERO_OF_THE_VILLAGE, 48000, badOmenLevel - 1, false, true));
							}
						}
					}
				}
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
						raidEvent.setPercent(0.0F);
						raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
					} else {
						raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
					}
				}
			}
			
		}
	}
	
	public static boolean isVillage(World world, BlockPos center) {
		for (Village village : world.getVillageCollection().getVillageList()){
			BlockPos vilCenter = village.getCenter();
			if (vilCenter.getDistance(center.getX(), center.getY(), center.getZ()) < 40) return true;
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
		for(int i = 0; i < 3; ++i) {
			BlockPos blockpos = findRandomSpawnPos(p_37764_, 1);
			if (blockpos != null) {
				return Optional.of(blockpos);
			}
		}
		
		return Optional.empty();
	}
	
	private boolean hasMoreWaves() {
		if (hasBonusWave()) {
			return !hasSpawnedBonusWave();
		} else {
			return !isFinalWave();
		}
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
				if (entity.isEntityAlive() && entity.world.provider.getDimension() == world.provider.getDimension() && !(center.distanceSq(blockpos) >= 12544.0D)) {
					if (entity.ticksExisted > 600) {
						if (world.getEntityFromUuid(entity.getUniqueID()) == null) set.add(entity);
						if (!isVillage(world, blockpos) && entity.getIdleTime() > 2400) {
							if (entity.hasCapability(RaidsContent.RAIDER, null)) {
								Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
								raider.setTicksOutsideRaid(raider.getTicksOutsideRaid() + 1);
							}
						}
						if (entity.hasCapability(RaidsContent.RAIDER, null) &&
								entity.getCapability(RaidsContent.RAIDER, null).getTicksOutsideRaid() >= 30) set.add(entity);
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
			double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
			double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
			if ((d0 <= 64.0D || collection.contains(player)) && player instanceof EntityPlayerMP)
				PacketHandler.NETWORK_INSTANCE.sendTo(new RaidSoundMessage(new BlockPos(d0, d1, d2)), (EntityPlayerMP)player);
		}
	}
	
	
	public void joinRaid(int wave, EntityLiving entity, @Nullable BlockPos pos, boolean p_37717_) {
		if (addWaveMob(wave, entity)) {
			Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
			raider.setCurrentRaid(this);
			raider.setWave(wave);
			raider.setTicksOutsideRaid(0);
		}
	}
	
	public void updateBossbar() {
		Set toRemove = Sets.newHashSet();
		for (Set<EntityLiving> set : groupEntityLivingMap.values()) {
			for (EntityLiving entity : set) {
				if (!entity.isAddedToWorld() |! entity.isEntityAlive()) {
					toRemove.add(entity);
					totalHealth -= entity.getHealth();
				}
			}
			set.removeAll(toRemove);
		}
		raidEvent.setPercent(MathUtils.clamp(getHealthOfEntityLivingLivings() / totalHealth, 0.0F, 1.0F));
	}
	
	public float getHealthOfEntityLivingLivings() {
		float f = 0.0F;
		for (Set<EntityLiving> set : groupEntityLivingMap.values()) for (EntityLiving entity : set) f += entity.getHealth();
		return f;
	}
	
	private boolean shouldSpawnGroup() {
		return raidCooldownTicks == 0 && (groupsSpawned < numGroups || shouldSpawnBonusGroup()) && getTotalEntityLivingsAlive() == 0;
	}
	
	public int getTotalEntityLivingsAlive() {
		return groupEntityLivingMap.values().stream().mapToInt(Set::size).sum();
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
	public EntityLiving getLeader(int p_37751_) {
		return groupToLeaderMap.get(p_37751_);
	}
	
	@Nullable
	private BlockPos findRandomSpawnPos(int p_37708_, int p_37709_) {
		int i = p_37708_ == 0 ? 2 : 2 - p_37708_;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		for(int i1 = 0; i1 < p_37709_; ++i1) {
			float f = world.rand.nextFloat() * ((float)Math.PI * 2F);
			int j = center.getX() + (int)Math.floor(Math.cos(f) * 32.0F * (float)i) + world.rand.nextInt(5);
			int l = center.getZ() + (int)Math.floor(Math.sin(f) * 32.0F * (float)i) + world.rand.nextInt(5);
			int k = world.getHeight(j, l);
			blockpos$mutableblockpos.setPos(j, k, l);
			if (isVillage(world, blockpos$mutableblockpos) || p_37708_ >= 2) {
				if (world.isAreaLoaded(new StructureBoundingBox(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getZ() - 10,
						blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getZ() + 10))
						&& world.isAirBlock(blockpos$mutableblockpos)) return blockpos$mutableblockpos;
			}
		}
		return null;
	}
	
	private boolean addWaveMob(int index, EntityLiving entity) {
		return addWaveMob(index, entity, true);
	}
	
	public boolean addWaveMob(int index, EntityLiving entity, boolean addHealth) {
		if (!entity.hasCapability(RaidsContent.RAIDER, null) |! entity.isAddedToWorld()) return false;
		groupEntityLivingMap.computeIfAbsent(index, v -> Sets.newHashSet());
		Set<EntityLiving> set = groupEntityLivingMap.get(index);
		EntityLiving EntityLiving = null;
		for(EntityLiving EntityLiving1 : set) {
			if (EntityLiving1.getUniqueID().equals(entity.getUniqueID())) {
				EntityLiving = EntityLiving1;
				break;
			}
		}
		if (EntityLiving != null) {
			set.remove(EntityLiving);
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
	
	private void setCenter(BlockPos p_37761_) {
		center = p_37761_;
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
		nbt.setInteger("BadOmenworld", badOmenLevel);
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
		for(UUID uuid : heroesOfTheVillage) {
			NBTTagCompound uuidnbt = new NBTTagCompound();
			uuidnbt.setLong("Most", uuid.getMostSignificantBits());
			uuidnbt.setLong("Least", uuid.getLeastSignificantBits());
			listtag.appendTag(uuidnbt);
		}
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
	
	public int getMaxBadOmenLevel() {
		return 5;
	}
	
	public List<String> getEntityStrings() {
		List<String> result = new ArrayList<>();
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
				if (entitylist.indexOf(entity) < entitylist.size()-1) builder.append(", ");
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