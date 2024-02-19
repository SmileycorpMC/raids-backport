package net.smileycorp.raids.common.raid;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsLogger;

import javax.annotation.Nullable;
import java.util.*;

public class RaidHandler {

	public static final NonNullList<Class<? extends EntityLiving>> RAIDERS = NonNullList.create();

	private static final List<RaidEntry> ENTRIES = new ArrayList();
	
	public static final Map<Class<? extends EntityLiving>, RaidBuffs> RAID_BUFFS = Maps.newHashMap();

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] count, @Nullable RaidEntry.Rider rider, @Nullable RaidEntry.BonusSpawns bonusSpawns) {
		ENTRIES.add(new RaidEntry(entity, count, rider, bonusSpawns));
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
	}
	
	public static <T extends EntityLiving> void registerRaidBuffs(Class<T> entity, RaidBuffs<T> buffs) {
		RAID_BUFFS.put(entity, buffs);
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
	}
	
	public static boolean isRaider(Entity entity) {
		return entity == null ? false : RAIDERS.contains(entity.getClass());
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
	
	public static void applyRaidBuffs(EntityLiving entity, Raid raid, int wave, Random rand) {
		RaidBuffs buffs = RAID_BUFFS.get(entity.getClass());
		if (buffs != null) buffs.apply(entity, raid, wave, rand);
	}
	
	public interface RaidBuffs<T extends EntityLiving> {
		
		void apply(T entity, Raid raid, int wave, Random rand);
	
	}
	
}
