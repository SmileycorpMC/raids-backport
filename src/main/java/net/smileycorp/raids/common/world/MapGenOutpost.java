package net.smileycorp.raids.common.world;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
import net.smileycorp.raids.common.RaidsLogger;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.config.OutpostConfig;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MapGenOutpost extends MapGenStructure {
    
    private static final List<Biome.SpawnListEntry> spawnlist = Lists.newArrayList(new Biome.SpawnListEntry(EntityPillager.class, 1, 1, 1));
    
    private static MapGenOutpost INSTANCE;
    private final ChunkGeneratorOverworld generator;
    
    private final List<Biome> spawnbiomes = Lists.newArrayList();
    
    public MapGenOutpost(ChunkGeneratorOverworld generator) {
        this.generator = generator;
        spawnbiomes.addAll(MapGenVillage.VILLAGE_SPAWN_BIOMES);
        spawnbiomes.add(Biomes.TAIGA_HILLS);
        spawnbiomes.add(Biomes.REDWOOD_TAIGA);
        spawnbiomes.add(Biomes.REDWOOD_TAIGA_HILLS);
        spawnbiomes.add(Biomes.DESERT_HILLS);
        spawnbiomes.add(Biomes.ICE_PLAINS);
        spawnbiomes.add(Biomes.MUTATED_PLAINS);
        spawnbiomes.add(Biomes.EXTREME_HILLS_EDGE);
    }
    
    @Override
    public String getStructureName() {
        return "PillagerOutposts";
    }
    
    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        return null;
    }
    
    public List<Biome.SpawnListEntry> getSpawnList() {
        return spawnlist;
    }
    
    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int x = chunkX;
        int z = chunkZ;
        if (x < 0) x -= OutpostConfig.maxDistance - 1;
        if (z < 0) z -= OutpostConfig.maxDistance - 1;
        int dx = (x / OutpostConfig.maxDistance);
        int dz = (z / OutpostConfig.maxDistance);
        Random random = world.setRandomSeed(dx, dz, 165745296);
        dx = dx * OutpostConfig.maxDistance + random.nextInt(OutpostConfig.maxDistance - OutpostConfig.maxDistance/4);
        dz = dz * OutpostConfig.maxDistance + random.nextInt(OutpostConfig.maxDistance - OutpostConfig.maxDistance/4);
        if (chunkX != dx || chunkZ != dz) return false;
        BlockPos pos = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
        if (!world.getBiomeProvider().areBiomesViable(pos.getX(), pos.getZ(), 0, spawnbiomes)) return false;
        return world.findNearestStructure("Village", pos, true).distanceSq(pos) >= (OutpostConfig.distanceFromVillage * OutpostConfig.distanceFromVillage);
    }
    
    public boolean canSpawn(World world, BlockPos pos) {
        OutpostStart structure = getStructureAt(pos);
        if (structure == null) return true;
        List<Entity> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, structure.getSpawnBox(), e -> {
            for (Biome.SpawnListEntry entry : spawnlist) if (entry.entityClass == e.getClass()) return true;
            return false;
        });
        return entities.size() < 8;
    }
    
    @Override
    protected OutpostStart getStructureAt(BlockPos pos) {
        for (StructureStart structure : structureMap.values()) if (((OutpostStart)structure).isInStructure(pos)) return (OutpostStart) structure;
        return null;
    }
    
    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new OutpostStart(world, rand, chunkX, chunkZ, generator);
    }
    
    public static MapGenOutpost getInstance(IChunkGenerator generator) {
        if (INSTANCE == null) INSTANCE = new MapGenOutpost((ChunkGeneratorOverworld) generator);
        if (INSTANCE.generator != generator) INSTANCE = new MapGenOutpost((ChunkGeneratorOverworld) generator);
        return INSTANCE;
    }
    
    public static class OutpostStart extends StructureStart {
    
        private final AxisAlignedBB boundingBox;
        
        public OutpostStart(World world, Random rand, int chunkX, int chunkZ, ChunkGeneratorOverworld generator) {
            int x = chunkX << 4;
            int z = chunkZ << 4;
            ChunkPrimer chunkprimer = new ChunkPrimer();
            generator.setBlocksInChunk(chunkX, chunkZ, chunkprimer);
            int y = getY(1, 1, 13, 13, chunkprimer);
            BlockPos center = new BlockPos(x + 8, y + 16, z + 8);
            BlockPos pos = new BlockPos(x, y, z);
            RaidsLogger.logInfo("Generated outpost at " + pos);
            components.addAll(StructureOutpostPieces.watchtower(world.getSaveHandler().getStructureTemplateManager(), pos,
                    Rotation.values()[rand.nextInt(Rotation.values().length)]));
            boundingBox = new AxisAlignedBB(center.add(-36, -26, -36), center.add(36, 26, 36));
            updateBoundingBox();
        }
        
        private boolean isInStructure(BlockPos pos) {
            return boundingBox.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
        }
    
        public int getY(int x, int z, int i, int k, ChunkPrimer primer) {
            int y0 = primer.findGroundBlockIdx(x, z);
            int yj = primer.findGroundBlockIdx(x, z + k);
            int jy = primer.findGroundBlockIdx(x + i, z);
            int j = primer.findGroundBlockIdx(x + i, z + k);
            return Math.min(Math.min(y0, yj), Math.min(jy, j));
        }
    
        public AxisAlignedBB getSpawnBox() {
            return boundingBox;
        }
        
    }
    
}
