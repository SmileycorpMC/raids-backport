package net.smileycorp.raids.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.smileycorp.raids.common.capability.IRaid;

public class RaidHandler {
	
	protected static final NonNullList<Class<? extends EntityLiving>> CAPABILITY_ENTITIES = NonNullList.<Class<? extends EntityLiving>>create();
	
	private static final List<RaidEntry> ENTRIES = new ArrayList<RaidEntry>();
	
	public static void registerEntry(Class<? extends EntityLiving> entity, @Nullable Class<? extends EntityLiving> mount, int[] min, int[] max, @Nullable BonusSpawns bonusSpawns) {
		ENTRIES.add(new RaidEntry(entity, mount, min, max, bonusSpawns));
		if (!CAPABILITY_ENTITIES.contains(entity) && entity!=null) CAPABILITY_ENTITIES.add(entity);
		if (!CAPABILITY_ENTITIES.contains(mount) && mount!=null) CAPABILITY_ENTITIES.add(mount);
	}
	
	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max, @Nullable BonusSpawns bonusSpawns) {
		registerEntry(entity, null, min, max, bonusSpawns);
	}
	
	public static void registerEntry(Class<? extends EntityLiving> entity, @Nullable Class<? extends EntityLiving> mount, int[] min, int[] max) {
		registerEntry(entity, mount, min, max, null);
	}
	
	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max) {
		registerEntry(entity, null, min, max, null);
	}

	public static List<EntityLiving> createNewWave(Village village, int wave, int level, boolean isBonusWave) {
		
		List<EntityLiving> entities = new ArrayList<EntityLiving>();
		Random rand = village.world.rand;
		BlockPos pos = village.getCenter();
		for (RaidEntry entry : ENTRIES) {
			for (int i = 0; i<entry.getCount(rand, village, wave, isBonusWave); i++) {
				try {
					entry.spawnEntity(rand, village, pos.north(rand.nextInt(6)-3).east(rand.nextInt(6)-3), entities, level);
				} catch (Exception e) {
					Raids.logError("Could not spawn entity for entry " + entry, e);
				}
			}
		}
		Collections.shuffle(entities);
		chooseRaidLeader(entities);
		return entities;
	}

	private static void chooseRaidLeader(List<EntityLiving> entities) {
		for (EntityLiving entity : entities) {
			if (entity instanceof EntityVindicator && entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null)) {
				entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null).setLeader();
				return;
			}
		}
		for (EntityLiving entity : entities) {
			if ((entity instanceof AbstractIllager) && entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null)) {
				entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null).setLeader();
				return;
			}
		}
		for (EntityLiving entity : entities) {
			if (entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null)) {
				entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null).setLeader();
				return;
			}
		}
	}

	public static int getWaveCount(World world) {
		switch(world.getDifficulty()) {
			case EASY: return 3;
			case NORMAL: return 5;
			case HARD: return 7;
			default: return 0;
		}
	}
	
	public static class RaidEntry {
		
		private Class<? extends EntityLiving> entity, mount;
		private int[] min;
		private int[] max;
		private BonusSpawns bonusSpawns;
		
		public RaidEntry(Class<? extends EntityLiving> entity, @Nullable Class<? extends EntityLiving> mount, int[] min, int[] max, @Nullable BonusSpawns bonusSpawns) {
			this.entity=entity;
			this.mount=mount;
			this.min=min;
			this.max=max;
			this.bonusSpawns=bonusSpawns;
		}
		
		public int getCount(Random rand, Village village, int wave, boolean isBonusWave) {
			EnumDifficulty difficulty = village.world.getDifficulty();
			int count = getCountWithoutBonus(difficulty, rand, village, wave);
			if (bonusSpawns!=null) count = bonusSpawns.apply(difficulty, rand, village, count, wave, isBonusWave);
			return count;
		}
		
		private int getCountWithoutBonus(EnumDifficulty difficulty, Random rand, Village village, int wave) {
			wave--;
			if (this.min.length<=wave||this.max.length<=wave) wave = Math.min(this.min.length, this.max.length);
			int min = this.min[wave];
			int max = this.max[wave];
			if (max == 0 || max < min) return 0;
			if (max == min) return min;
			if (difficulty == EnumDifficulty.HARD && !(min == 0 && max == 1) && wave < 5) max++;
			int count = min;
			for (int i = 0; i< max-min; i++) if (rand.nextInt(difficulty == EnumDifficulty.EASY ? 4 : 2) == 0) count++;
			return count;
		}
		
		public void spawnEntity(Random rand, Village village, BlockPos pos, List<EntityLiving> entities, int level) throws Exception {
			World world = village.world;
			IRaid raid  = null;
			if (village.hasCapability(RaidsContent.RAID_CAPABILITY, null)) raid = village.getCapability(RaidsContent.RAID_CAPABILITY, null);
			EntityLiving entity = this.entity.getConstructor(World.class).newInstance(world);
			entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
			world.spawnEntity(entity);
			entities.add(entity);
			if (entity.hasCapability(RaidsContent.RAIDER_CAPABILITY, null) && raid!=null) entity.getCapability(RaidsContent.RAIDER_CAPABILITY, null).setRaid(raid);
			if (mount!=null) {
				EntityLiving mount = this.mount.getConstructor(World.class).newInstance(world);
				mount.setPosition(pos.getX(), pos.getY(), pos.getZ());
				if (mount.hasCapability(RaidsContent.RAIDER_CAPABILITY, null) && raid!=null) mount.getCapability(RaidsContent.RAIDER_CAPABILITY, null).setRaid(raid);
				world.spawnEntity(mount);
				entity.startRiding(mount, true);
				entities.add(mount);
			}
		}
	}
	@FunctionalInterface
	public static interface BonusSpawns {
		int apply(EnumDifficulty difficulty, Random rand, Village village, int wave, int count, boolean isBonusWave);
	}
}
