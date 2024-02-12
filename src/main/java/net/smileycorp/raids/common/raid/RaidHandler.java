package net.smileycorp.raids.common.raid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RaidHandler {

	public static final NonNullList<Class<? extends EntityLiving>> RAIDERS = NonNullList.create();

	private static final List<RaidEntry> ENTRIES = new ArrayList();

	public static void registerEntry(Class<? extends EntityLiving> entity, Class<? extends EntityLiving> mount, int[] min, int[] max, RaidEntry.BonusSpawns bonusSpawns) {
		ENTRIES.add(new RaidEntry(entity, mount, min, max, bonusSpawns));
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
		if (!RAIDERS.contains(mount) && mount != null) RAIDERS.add(mount);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max, RaidEntry.BonusSpawns bonusSpawns) {
		registerEntry(entity, null, min, max, bonusSpawns);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, Class<? extends EntityLiving> mount, int[] min, int[] max) {
		registerEntry(entity, mount, min, max, null);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max) {
		registerEntry(entity, null, min, max, null);
	}

	public static void spawnNewWave(Raid raid, BlockPos pos, int wave, boolean isBonusWave) {
		List<EntityLiving> entities = new ArrayList<EntityLiving>();
		Random rand = raid.getWorld().rand;
		for (RaidEntry entry : ENTRIES) {
			for (int i = 0; i < entry.getCount(raid, rand, wave, isBonusWave); i++) {
				try {
					entry.spawnEntity(raid, wave, raid.getWorld().getHeight(pos.north(rand.nextInt(6)-3).east(rand.nextInt(6)-3)), entities);
				} catch (Exception e) {
					RaidsLogger.logError("Could not spawn entity for entry " + entry, e);
				}
			}
		}
		Collections.shuffle(entities);
		chooseRaidLeader(raid, wave, entities);
	}

	private static void chooseRaidLeader(Raid raid, int wave, List<EntityLiving> entities) {
		for (EntityLiving entity : entities) {
			if (entity instanceof EntityVindicator) {
				raid.setLeader(wave, entity);
				return;
			}
		}
		for (EntityLiving entity : entities) {
			if (entity instanceof AbstractIllager) {
				raid.setLeader(wave, entity);
				return;
			}
		}
		for (EntityLiving entity : entities) {
		if (entity.hasCapability(RaidsContent.RAIDER, null)) {
				raid.setLeader(wave, entity);
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
	
	public static boolean isRaider(Entity entity) {
		return entity == null ? false : RAIDERS.contains(entity.getClass());
	}
	
}
