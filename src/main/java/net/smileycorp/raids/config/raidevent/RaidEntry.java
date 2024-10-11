package net.smileycorp.raids.config.raidevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class RaidEntry {
    
    private final NBTTagCompound nbt;
    private final int[] count;
    private final Value<ResourceLocation> rider;
    private final Value<Integer> bonusSpawns;
    private Map<Class<? extends EntityLiving>, Integer> numSpawned = Maps.newHashMap();
    
    public RaidEntry(ResourceLocation entity, @Nullable NBTTagCompound nbt, int[] count, @Nullable Value<ResourceLocation> rider, @Nullable Value<Integer> bonusSpawns) throws Exception {
        if (GameData.getEntityRegistry().getValue(entity) == null) {
            throw new Exception("Entry is null ");
        }
        this.nbt = nbt == null ? new NBTTagCompound() : nbt;
        nbt.setString("id", entity.toString());
        this.count = count;
        this.rider = rider;
        this.bonusSpawns = bonusSpawns;
    }
    
    public int getCount(Raid raid, int wave, BlockPos pos, boolean isBonusWave) {
        numSpawned.clear();
        int count = getCountWithoutBonus(raid, wave, isBonusWave);
        if (bonusSpawns != null) {
            int bonus = bonusSpawns.get(RaidContext.Builder.of(raid).wave(wave).bonus(isBonusWave).pos(pos).build());
            if (bonus > 0) count += raid.getRandom().nextInt(bonus + 1);
        }
        return count;
    }
    
    private int getCountWithoutBonus(Raid raid, int wave, boolean shouldSpawnBonus) {
        if (shouldSpawnBonus) wave = raid.getNumGroups();
        return wave > count.length ? count[count.length - 1] : count[wave - 1];
    }
    
    public void spawnEntity(Raid raid, int wave, BlockPos pos, List<EntityLiving> entities, boolean isBonusWave) throws Exception {
        World world = raid.getWorld();
        EntityLiving entity = (EntityLiving) this.entity.newInstance(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        if (world.spawnEntity(entity)) {
            entities.add(entity);
            entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            raid.joinRaid(wave, entity, false);
        }
        if (rider != null) {
            ResourceLocation loc = this.rider.get(RaidContext.Builder.of(raid).spawned(numSpawned).bonus(isBonusWave).pos(pos).build());
            EntityEntry entry = GameData.getEntityRegistry().getValue(loc);
            if (entry == null) return;
            Entity rider = entry.newInstance(world);
            if (rider == null |!(rider instanceof EntityLiving)) return;
            EntityLiving riderLiving = (EntityLiving) rider;
            numSpawned.putIfAbsent(riderLiving.getClass(), 0);
            numSpawned.put(riderLiving.getClass(), numSpawned.get(riderLiving.getClass()) + 1);
            riderLiving.setPosition(pos.getX(), pos.getY(), pos.getZ());
            if (world.spawnEntity(riderLiving)) {
                riderLiving.onInitialSpawn(world.getDifficultyForLocation(pos), null);
                entities.add(riderLiving);
                raid.joinRaid(wave, riderLiving, false);
                riderLiving.startRiding(entity, true);
            }
        }
    }
    
    public EntityEntry getEntity() {
        return entity;
    }
    
    public static RaidEntry deserialize(JsonObject json) {
        try {
            EntityEntry entity = GameData.getEntityRegistry().getValue(new ResourceLocation(json.get("entity").getAsString()));
            if (!EntityLiving.class.isAssignableFrom(entity.getEntityClass())) throw new Exception(json.get("entity").getAsString() + " is not an instanceof EntityLiving");
            List<Integer> count = Lists.newArrayList();
            json.get("spawn_counts").getAsJsonArray().forEach(e -> count.add(e.getAsInt()));
            Value<ResourceLocation> rider = ValueRegistry.INSTANCE.readValue(DataType.RESOURCE_LOCATION, json.get("rider"));
            Value<Integer> bonusSpawns = ValueRegistry.INSTANCE.readValue(DataType.INT, json.get("bonus_spawns"));
            RaidHandler.addRaider((Class<? extends EntityLiving>) entity.getEntityClass());
            return new RaidEntry(entity, count.stream().mapToInt(i->i).toArray(), rider, bonusSpawns);
        } catch (Exception e) {
            RaidsLogger.logError("Failed to read raid entry " + json, e);
        }
        return null;
    }
    
}
