package net.smileycorp.raids.common.raid;

import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;

import java.util.Random;

public interface BonusSpawns {
    
    int apply(EnumDifficulty difficulty, Random rand, Village village, int wave, int count, boolean isBonusWave);
    
}
