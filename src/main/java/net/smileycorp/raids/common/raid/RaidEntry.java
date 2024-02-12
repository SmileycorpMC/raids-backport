package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsContent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RaidEntry {
    
    private Class<? extends EntityLiving> entity, mount;
    private int[] min;
    private int[] max;
    private BonusSpawns bonusSpawns;
    
    public RaidEntry(Class<? extends EntityLiving> entity, @Nullable Class<? extends EntityLiving> mount, int[] min, int[] max, @Nullable BonusSpawns bonusSpawns) {
        this.entity = entity;
        this.mount = mount;
        this.min = min;
        this.max = max;
        this.bonusSpawns = bonusSpawns;
    }
    
    public int getCount(Raid raid, Random rand, int wave, boolean isBonusWave) {
        EnumDifficulty difficulty = raid.getWorld().getDifficulty();
        int count = getCountWithoutBonus(difficulty, rand, raid, wave);
        if (bonusSpawns != null) count = bonusSpawns.apply(difficulty, rand, raid, count, wave, isBonusWave);
        return count;
    }
    
    private int getCountWithoutBonus(EnumDifficulty difficulty, Random rand, Raid raid, int wave) {
        if (min.length <= wave || max.length <= wave) wave = Math.min(min.length, max.length);
        int min = this.min[wave];
        int max = this.max[wave];
        if (max == 0 || max < min) return 0;
        if (max == min) return min;
        if (difficulty == EnumDifficulty.HARD && !(min == 0 && max == 1) && wave < 5) max++;
        int count = min;
        for (int i = 0; i < max - min; i++) if (rand.nextInt(difficulty == EnumDifficulty.EASY ? 4 : 2) == 0) count++;
        return count;
    }
    
    public void spawnEntity(Raid raid, int wave, BlockPos pos, List<EntityLiving> entities) throws Exception {
        World world = raid.getWorld();
        EntityLiving entity = this.entity.getConstructor(World.class).newInstance(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        world.spawnEntity(entity);
        entities.add(entity);
        entity.setGlowing(true);
        entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
        raid.joinRaid(wave, entity, pos, false);
        if (entity.hasCapability(RaidsContent.RAIDER, null) && raid != null)
            entity.getCapability(RaidsContent.RAIDER, null).setCurrentRaid(raid);
        if (mount != null) {
            EntityLiving mount = this.mount.getConstructor(World.class).newInstance(world);
            mount.setPosition(pos.getX(), pos.getY(), pos.getZ());
            if (mount.hasCapability(RaidsContent.RAIDER, null) && raid != null)
                mount.getCapability(RaidsContent.RAIDER, null).setCurrentRaid(raid);
            world.spawnEntity(mount);
            mount.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            mount.setGlowing(true);
            entity.startRiding(mount, true);
            entities.add(mount);
            raid.joinRaid(wave, entity, pos, false);
        }
    }
    
    public interface BonusSpawns {
        
        int apply(EnumDifficulty difficulty, Random rand, Raid raid, int wave, int count, boolean isBonusWave);
        
    }
}
