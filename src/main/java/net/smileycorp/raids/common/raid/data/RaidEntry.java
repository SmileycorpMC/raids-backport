package net.smileycorp.raids.common.raid.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.util.RaidsLogger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RaidEntry {
    
    private final Class<? extends EntityLiving> entity;
    private final int[] count;
    private final Rider rider;
    private final BonusSpawns bonusSpawns;
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
        if (shouldSpawnBonus) wave = raid.getNumGroups();
        return wave >= count.length ? count[count.length - 1] : count[wave];
    }
    
    public void spawnEntity(Raid raid, int wave, BlockPos pos, List<EntityLiving> entities) throws Exception {
        World world = raid.getWorld();
        EntityLiving entity = this.entity.getConstructor(World.class).newInstance(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        if (world.spawnEntity(entity)) {
            entities.add(entity);
            entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            raid.joinRaid(wave, entity, false);
        }
        if (rider != null) {
            EntityLiving rider = this.rider.apply(raid, world, numSpawned);
            if (rider == null) return;
            numSpawned.putIfAbsent(rider.getClass(), 0);
            numSpawned.put(rider.getClass(), numSpawned.get(rider.getClass()) + 1);
            rider.setPosition(pos.getX(), pos.getY(), pos.getZ());
            if (world.spawnEntity(rider)) {
                rider.onInitialSpawn(world.getDifficultyForLocation(pos), null);
                entities.add(rider);
                raid.joinRaid(wave, rider, false);
                rider.startRiding(entity, true);
            }
        }
    }
    
    public static RaidEntry deserialize(JsonObject json) {
        try {
            Class<? extends EntityLiving> entity = (Class<? extends EntityLiving>) GameData.getEntityRegistry().getValue(new ResourceLocation(json.get("entity").getAsString())).getEntityClass();
            if (!EntityLiving.class.isAssignableFrom(entity)) throw new Exception(json.get("entity").getAsString() + " is not an instanceof EntityLiving");
            List<Integer> count = Lists.newArrayList();
            json.get("spawn_counts").getAsJsonArray().forEach(e -> count.add(e.getAsInt()));
            Rider rider = null;
            BonusSpawns bonus = null;
            return new RaidEntry(entity, count.stream().mapToInt(i->i).toArray(), rider, bonus);
        } catch (Exception e) {
            RaidsLogger.logError("Failed to read raid entry " + json, e);
        }
        return null;
    }
    
    public interface BonusSpawns {
        
        int apply(EnumDifficulty difficulty, Random rand, Raid raid, int wave, boolean isBonusWave);
        
    }
    
    public interface Rider {
    
        EntityLiving apply(Raid raid, World world, Map<Class<? extends EntityLiving>, Integer> numSpawned);
        
    }
}
