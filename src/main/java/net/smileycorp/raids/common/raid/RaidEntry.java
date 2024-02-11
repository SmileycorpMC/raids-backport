package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.entity.ai.EntityAIGoToPos;
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
    
    public int getCount(Random rand, Village village, int wave, boolean isBonusWave) {
        EnumDifficulty difficulty = village.world.getDifficulty();
        int count = getCountWithoutBonus(difficulty, rand, village, wave);
        if (bonusSpawns != null) count = bonusSpawns.apply(difficulty, rand, village, count, wave, isBonusWave);
        return count;
    }
    
    private int getCountWithoutBonus(EnumDifficulty difficulty, Random rand, Village village, int wave) {
        wave--;
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
    
    public void spawnEntity(Random rand, Village village, BlockPos pos, List<EntityLiving> entities, int level) throws Exception {
        World world = village.world;
        Raid raid = null;
        if (village.hasCapability(RaidsContent.RAID_CAPABILITY, null))
            raid = village.getCapability(RaidsContent.RAID_CAPABILITY, null);
        EntityLiving entity = this.entity.getConstructor(World.class).newInstance(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        world.spawnEntity(entity);
        entities.add(entity);
        entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
        entity.tasks.addTask(4, new EntityAIMoveThroughVillage((EntityCreature) entity, 1.0D, false));
        entity.tasks.addTask(5, new EntityAIGoToPos(entity, village.getCenter()));
        if (entity.hasCapability(RaidsContent.RAIDER, null) && raid != null)
            entity.getCapability(RaidsContent.RAIDER, null).setCurrentRaid(raid);
        if (mount != null) {
            EntityLiving mount = this.mount.getConstructor(World.class).newInstance(world);
            mount.setPosition(pos.getX(), pos.getY(), pos.getZ());
            if (mount.hasCapability(RaidsContent.RAIDER, null) && raid != null)
                mount.getCapability(RaidsContent.RAIDER, null).setCurrentRaid(raid);
            world.spawnEntity(mount);
            mount.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            entity.startRiding(mount, true);
            mount.tasks.addTask(4, new EntityAIMoveThroughVillage((EntityCreature) entity, 1.0D, false));
            mount.tasks.addTask(5, new EntityAIGoToPos(entity, village.getCenter()));
            entities.add(mount);
        }
    }
    
}
