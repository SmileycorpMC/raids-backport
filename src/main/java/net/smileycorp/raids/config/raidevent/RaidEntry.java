package net.smileycorp.raids.config.raidevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
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
    
    private final ResourceLocation entity;
    private final Value<String> nbt;
    private final int[] count;
    private final Value<String> rider;
    private final Value<Integer> bonusSpawns;
    private Map<Class<? extends EntityLiving>, Integer> numSpawned = Maps.newHashMap();
    
    public RaidEntry(ResourceLocation entity, @Nullable Value<String> nbt, int[] count, @Nullable Value<String> rider, @Nullable Value<Integer> bonusSpawns) throws Exception {
        EntityEntry entry = GameData.getEntityRegistry().getValue(entity);
        if (entry == null) throw new Exception("Entry is null for entity " + entity);
        if (!EntityLiving.class.isAssignableFrom(entry.getEntityClass())) throw new Exception(entity + " is not an instanceof EntityLiving");
        RaidHandler.addRaider((Class<? extends EntityLiving>) entry.getEntityClass());
        this.entity = entity;
        this.nbt = nbt;
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
        RaidContext ctx = RaidContext.Builder.of(raid).spawned(numSpawned).bonus(isBonusWave).pos(pos).build();
        World world = raid.getWorld();
        NBTTagCompound nbt;
        try {
            nbt = JsonToNBT.getTagFromJson(this.nbt.get(ctx));
        } catch (Exception e) {
            RaidsLogger.logError("Failed loading nbt for " + this, e);
            nbt = new NBTTagCompound();
        }
        nbt.setString("id", entity.toString());
        Entity entity = AnvilChunkLoader.readWorldEntityPos(nbt, world, pos.getX(), pos.getY(), pos.getZ(), false);
        if (!(entity instanceof EntityLiving)) return;
        EntityLiving living = (EntityLiving) entity;
        if (world.spawnEntity(living)) {
            entities.add(living);
            living.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            raid.joinRaid(wave, living, false);
        }
        if (rider != null) {
            String str = this.rider.get(ctx);
            NBTTagCompound riderNbt;
            try {
                riderNbt = JsonToNBT.getTagFromJson(str);
            } catch (Exception e) {
                riderNbt = new NBTTagCompound();
                riderNbt.setString("id", str);
            }
            Entity rider = AnvilChunkLoader.readWorldEntityPos(riderNbt, world, pos.getX(), pos.getY(), pos.getZ(), false);
            if (rider == null |!(rider instanceof EntityLiving)) return;
            EntityLiving riderLiving = (EntityLiving) rider;
            numSpawned.putIfAbsent(riderLiving.getClass(), 0);
            numSpawned.put(riderLiving.getClass(), numSpawned.get(riderLiving.getClass()) + 1);
            if (world.spawnEntity(riderLiving)) {
                riderLiving.onInitialSpawn(world.getDifficultyForLocation(pos), null);
                entities.add(riderLiving);
                raid.joinRaid(wave, riderLiving, false);
                riderLiving.startRiding(living, true);
            }
        }
    }
    
    public EntityEntry getEntity() {
        return GameData.getEntityRegistry().getValue(entity);
    }
    
    public static RaidEntry deserialize(JsonObject json) {
        try {
            ResourceLocation entity = new ResourceLocation(json.get("entity").getAsString());
            Value<String> nbt = ValueRegistry.INSTANCE.readValue(DataType.STRING, json.get("nbt"));
            List<Integer> count = Lists.newArrayList();
            json.get("spawn_counts").getAsJsonArray().forEach(e -> count.add(e.getAsInt()));
            Value<String> rider = ValueRegistry.INSTANCE.readValue(DataType.STRING, json.get("rider"));
            Value<Integer> bonusSpawns = ValueRegistry.INSTANCE.readValue(DataType.INT, json.get("bonus_spawns"));
            return new RaidEntry(entity, nbt, count.stream().mapToInt(i->i).toArray(), rider, bonusSpawns);
        } catch (Exception e) {
            RaidsLogger.logError("Failed to read raid entry " + json, e);
        }
        return null;
    }
    
}
