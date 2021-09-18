package net.smileycorp.raids.common;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class RaidWaves {

	public static void createNewWave(Village village, List<EntityLiving> entities, int level) {
		
	}

	public static int getWaveCount(World world) {
		switch(world.getDifficulty()) {
			case EASY: {
				return 3;
			} case HARD:
				return 5;
			case NORMAL: {
				return 7;
			}
			default: return 0;
			
		}
		
	}

}
