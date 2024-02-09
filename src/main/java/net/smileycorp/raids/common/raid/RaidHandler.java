package net.smileycorp.raids.common.raid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.raids.common.Raids;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidSoundMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RaidHandler {

	public static final NonNullList<Class<? extends EntityLiving>> RAIDERS = NonNullList.create();

	private static final List<RaidEntry> ENTRIES = new ArrayList();

	public static void registerEntry(Class<? extends EntityLiving> entity, Class<? extends EntityLiving> mount, int[] min, int[] max, BonusSpawns bonusSpawns) {
		ENTRIES.add(new RaidEntry(entity, mount, min, max, bonusSpawns));
		if (!RAIDERS.contains(entity) && entity != null) RAIDERS.add(entity);
		if (!RAIDERS.contains(mount) && mount != null) RAIDERS.add(mount);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max, BonusSpawns bonusSpawns) {
		registerEntry(entity, null, min, max, bonusSpawns);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, Class<? extends EntityLiving> mount, int[] min, int[] max) {
		registerEntry(entity, mount, min, max, null);
	}

	public static void registerEntry(Class<? extends EntityLiving> entity, int[] min, int[] max) {
		registerEntry(entity, null, min, max, null);
	}

	public static List<EntityLiving> createNewWave(Village village, int wave, int level, boolean isBonusWave) {
		List<EntityLiving> entities = new ArrayList<EntityLiving>();
		Random rand = village.world.rand;
		Vec3d dir = DirectionUtils.getRandomDirectionVecXZ(rand);
		BlockPos center = village.getCenter();
		BlockPos pos = DirectionUtils.getClosestLoadedPos(village.world, center,  dir, 64);
		for (RaidEntry entry : ENTRIES) {
			for (int i = 0; i < entry.getCount(rand, village, wave, isBonusWave); i++) {
				try {
					entry.spawnEntity(rand, village, pos.north(rand.nextInt(6)-3).east(rand.nextInt(6)-3), entities, level);
				} catch (Exception e) {
					Raids.logError("Could not spawn entity for entry " + entry, e);
				}
			}
		}
		Collections.shuffle(entities);
		chooseRaidLeader(entities);
		PacketHandler.NETWORK_INSTANCE.sendToAllAround(new RaidSoundMessage(), new TargetPoint(village.world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 96));
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
	
	public static boolean isRaider(Entity entity) {
		return entity == null ? false : RAIDERS.contains(entity.getClass());
	}
	
}
