package net.smileycorp.raids.common.raid;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RaidEntry {
    
    private Class<? extends EntityLiving> entity;
    private int[] count;
    private Rider rider;
    private BonusSpawns bonusSpawns;
    private Map<Class<? extends EntityLiving>, Integer> numSpawned = Maps.newHashMap();
    
    public RaidEntry(Class<? extends EntityLiving> entity, int[] count, @Nullable Rider rider, @Nullable BonusSpawns bonusSpawns) {
        this.entity = entity;
        this.count = count;
        this.rider = rider;
        this.bonusSpawns = bonusSpawns;
    }
    
    public int getCount(Raid raid, Random rand, int wave, boolean shouldSpawnBonus) {
        numSpawned.clear();
        EnumDifficulty difficulty = raid.getWorld().getDifficulty();
        int count = getCountWithoutBonus(raid, wave, shouldSpawnBonus);
        if (bonusSpawns != null) {
            int bonus = bonusSpawns.apply(difficulty, rand, raid, wave, shouldSpawnBonus);
            if (bonus > 0) count += rand.nextInt(bonus + 1);
        }
        return count;
    }
    
    private int getCountWithoutBonus(Raid raid, int wave, boolean shouldSpawnBonus) {
        return shouldSpawnBonus ? count[raid.getNumGroups()] : count[wave];
    }
    
    public void spawnEntity(Raid raid, int wave, BlockPos pos, List<EntityLiving> entities) throws Exception {
        World world = raid.getWorld();
        EntityLiving entity = this.entity.getConstructor(World.class).newInstance(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        if (world.spawnEntity(entity)) {
            entities.add(entity);
            entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            raid.joinRaid(wave, entity, pos, false);
        }
        if (rider != null) {
            EntityLiving rider = this.rider.apply(raid, world, numSpawned);
            if (rider == null) return;
            numSpawned.putIfAbsent(rider.getClass(), 0);
            numSpawned.put(rider.getClass(), numSpawned.get(rider.getClass()) + 1);
            rider.setPosition(pos.getX(), pos.getY(), pos.getZ());
            if (world.spawnEntity(rider)) {
                rider.onInitialSpawn(world.getDifficultyForLocation(pos), null);
                rider.startRiding(entity, true);
                entities.add(rider);
                raid.joinRaid(wave, rider, pos, false);
            }
        }
    }
    
    public interface BonusSpawns {
        
        int apply(EnumDifficulty difficulty, Random rand, Raid raid, int wave, boolean isBonusWave);
        
    }
    
    public interface Rider {
    
        EntityLiving apply(Raid raid, World world, Map<Class<? extends EntityLiving>, Integer> numSpawned);
        
    }
}
