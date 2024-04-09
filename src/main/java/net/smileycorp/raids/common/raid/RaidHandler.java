package net.smileycorp.raids.common.raid;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.raids.common.RaidsLogger;

import javax.annotation.Nullable;
import java.util.*;

public class RaidHandler {

	public static final NonNullList<Class<? extends EntityLiving>> RAIDERS = NonNullList.create();

	private static final Map<Class<? extends EntityLiving>, RaidEntry> ENTRIES = Maps.newHashMap();
	
	public static final Map<Class, RaidBuffs> RAID_BUFFS = Maps.newHashMap();

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] count, float captainChance, @Nullable RaidEntry.Rider rider, @Nullable RaidEntry.BonusSpawns bonusSpawns) {
		ENTRIES.put(entity, new RaidEntry(entity, count, captainChance, rider, bonusSpawns));
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
	}
	
	public static void registerRaidBuffs(Class item, RaidBuffs buffs) {
		RAID_BUFFS.put(item, buffs);
	}
	
	public static void addRaider(Class<? extends EntityLiving> entity) {
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
	}
	
	public static boolean isRaider(Entity entity) {
		return entity == null ? false : RAIDERS.contains(entity.getClass());
	}
	
	public static boolean canBeCaptain(EntityLiving entity) {
		return getCaptainChance(entity) > 0;
	}
	
	public static float getCaptainChance(EntityLiving entity) {
		if (!isRaider(entity)) return 0;
		RaidEntry entry = ENTRIES.get(entity.getClass());
		if (entry == null) return 0;
		return entry.getCaptainChance();
	}

	public static void spawnNewWave(Raid raid, BlockPos pos, int wave, boolean isBonusWave) {
		List<EntityLiving> entities = new ArrayList<EntityLiving>();
		Random rand = raid.getWorld().rand;
		for (RaidEntry entry : ENTRIES.values()) {
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
			if (canBeCaptain(entity)) {
				raid.setLeader(wave, entity);
				return;
			}
		}
	}
	
	public static void applyRaidBuffs(EntityLiving entity, Raid raid, int wave, Random rand) {
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			ItemStack stack = entity.getItemStackFromSlot(slot);
			if (stack == null) continue;
			if (stack.isEmpty()) continue;
			for (Map.Entry<Class, RaidBuffs> buffs : RAID_BUFFS.entrySet()) {
				if (buffs.getKey().isAssignableFrom(stack.getItem().getClass()))
					stack = buffs.getValue().apply(stack, entity, raid, wave, rand);
			}
			entity.setItemStackToSlot(slot, stack);
		}
	}
	
	public interface RaidBuffs {
		
		ItemStack apply(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand);
	
	}
	
}
