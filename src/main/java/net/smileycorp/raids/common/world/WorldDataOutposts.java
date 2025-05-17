package net.smileycorp.raids.common.world;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.storage.WorldSavedData;

import java.util.Map;

public class WorldDataOutposts extends WorldSavedData {
    
    public static final String DATA = "PillagerOutposts";
    
    private Map<Long, WorldGenOutpost.OutpostStart> outposts = Maps.newLinkedHashMap();
    
    public WorldDataOutposts(String data) {
        super(data);
    }
    
    public WorldDataOutposts() {
        this(DATA);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        //check if the nbt is in the MapGenStructureData format
        //if so read with backwards compatibility
        if (nbt.hasKey("Features")) {
            nbt.getKeySet().forEach(key -> addOutpost(new WorldGenOutpost.OutpostStart(nbt.getCompoundTag(key).getCompoundTag("center"))));
            return;
        }
        //normal loading
        nbt.getKeySet().forEach(key -> addOutpost(new WorldGenOutpost.OutpostStart(nbt.getCompoundTag(key))));
    }
    
    public WorldGenOutpost.OutpostStart getStructureAt(BlockPos pos) {
        for (WorldGenOutpost.OutpostStart outpost : outposts.values()) if (outpost.isInStructure(pos)) return outpost;
        return null;
    }
    
    public boolean isInOutpost(BlockPos pos) {
        for (WorldGenOutpost.OutpostStart outpost : outposts.values()) if (outpost.isInStructure(pos)) return true;
        return false;
    }
    
    public void addOutpost(WorldGenOutpost.OutpostStart outpost) {
        ChunkPos chunkPos = new ChunkPos(outpost.getCenter());
        outposts.put(ChunkPos.asLong(chunkPos.x, chunkPos.z), outpost);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for (WorldGenOutpost.OutpostStart outpost : outposts.values()) {
            BlockPos pos = outpost.getCenter();
            NBTTagCompound nbt = new NBTTagCompound();
            outpost.writeToNBT(new NBTTagCompound());
            compound.setTag(MapGenStructureData.formatChunkCoords(pos.getX() >> 4, pos.getZ() >> 4), nbt);
        }
        return compound;
    }
    
    public static WorldDataOutposts getData(WorldServer world) {
        WorldDataOutposts data = (WorldDataOutposts) world.getMapStorage().getOrLoadData(WorldDataOutposts.class, DATA);
        if (data == null) {
            data = new WorldDataOutposts();
            world.getMapStorage().setData(DATA, data);
        }
        return data;
    }
}
