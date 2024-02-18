package net.smileycorp.raids.common.raid;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsLogger;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WorldDataRaids extends WorldSavedData {
    
    public static final String DATA = Constants.MODID;
    
    private final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private WorldServer world;
    private int nextAvailableID;
    private int tick;
    private PatrolSpawner patrols = new PatrolSpawner();
    private NBTTagList raidNBT;
    
    public WorldDataRaids(String data) {
        super(data);
    }
    
    public WorldDataRaids() {
        this(DATA);
        nextAvailableID = 1;
    }
    
    private void setWorld(WorldServer world) {
        this.world = world;
        if (raidNBT != null) {
            for(NBTBase base : raidNBT) {
                NBTTagCompound compound = (NBTTagCompound) base;
                Raid raid = new Raid(world, compound);
                raidMap.put(raid.getId(), raid);
            }
            raidNBT = null;
        }
    }
    
    public Raid get(int id) {
        return raidMap.get(id);
    }
    
    public void tick() {
        tick++;
        patrols.tick(world);
        Iterator<Raid> iterator = raidMap.values().iterator();
        while (iterator.hasNext()) {
            Raid raid = iterator.next();
            if (raid.isStopped()) {
                iterator.remove();
                setDirty(true);
            } else raid.tick();
        }
        if (tick % 200 == 0) setDirty(true);
    }
    
    public static boolean canJoinRaid(EntityLiving entity, Raid raid) {
        if (entity != null && raid != null && raid.getWorld() != null) {
            return entity.isEntityAlive() && entity.hasCapability(RaidsContent.RAIDER, null) && entity.getIdleTime() <= 2400 &&
                    entity.world.provider.getDimension() == raid.getWorld().provider.getDimension();
        } else {
            return false;
        }
    }
    
    @Nullable
    public Raid createOrExtendRaid(EntityPlayerMP player) {
        if (player.isSpectator()) return null;
        else {
            Raid raid = getOrCreateRaid(world, world.getVillageCollection().getNearestVillage(player.getPosition(), 64).getCenter());
            boolean flag = false;
            if (!raid.isStarted()) {
                if (!raidMap.containsKey(raid.getId())) raidMap.put(raid.getId(), raid);
                flag = true;
            } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
                flag = true;
            } else player.removeActivePotionEffect(RaidsContent.BAD_OMEN);
            if (flag) raid.absorbBadOmen(player);
            setDirty(true);
            return raid;
        }
    }
    
    private Raid getOrCreateRaid(WorldServer world, BlockPos pos) {
        Raid raid = getRaidAt(pos);
        return raid != null ? raid : new Raid(getUniqueId(), world, pos);
    }
    
    public Raid getRaidAt(BlockPos pos) {
        return getNearbyRaid(pos, 9216);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        nextAvailableID = nbt.getInteger("NextAvailableID");
        tick = nbt.getInteger("Tick");
        if (world == null) raidNBT = nbt.getTagList("Raids", 10);
        else for(NBTBase base : nbt.getTagList("Raids", 10)) {
            NBTTagCompound compound = (NBTTagCompound) base;
            Raid raid = new Raid(world, compound);
            raidMap.put(raid.getId(), raid);
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("NextAvailableID", nextAvailableID);
        nbt.setInteger("Tick", tick);
        NBTTagList list = new NBTTagList();
        for(Raid raid : raidMap.values()) {
            NBTTagCompound compound = new NBTTagCompound();
            raid.save(compound);
            list.appendTag(compound);
        }
        nbt.setTag("Raids", list);
        return nbt;
    }
    
    private int getUniqueId() {
        return nextAvailableID++;
    }
    
    @Nullable
    public Raid getNearbyRaid(BlockPos p_37971_, int p_37972_) {
        Raid raid = null;
        double d0 = p_37972_;
        for(Raid raid1 : raidMap.values()) {
            double d1 = raid1.getCenter().distanceSq(p_37971_);
            if (raid1.isActive() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }
        return raid;
    }
    
    public void logDebug() {
        List<String> out = Lists.newArrayList();
        out.add(toString());
        out.add("Existing raids: {");
        for (Raid raid : raidMap.values()) {
            out.add("	" + raid.toString());
            out.addAll(raid.getEntityStrings());
        }
        out.add("}");
        RaidsLogger.writeToFile(out);
    }
    
    public static WorldDataRaids getData(WorldServer world) {
        WorldDataRaids data = (WorldDataRaids) world.getMapStorage().getOrLoadData(WorldDataRaids.class, DATA);
        if (data == null) {
            data = new WorldDataRaids();
            world.getMapStorage().setData(DATA, data);
        }
        if (data.world == null) {
            data.setWorld(world);
            data.setDirty(true);
        }
        return data;
    }
    
}
