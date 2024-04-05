package net.smileycorp.raids.common.world;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import net.smileycorp.raids.common.RaidsLogger;
import net.smileycorp.raids.config.OutpostConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class MapGenOutpost extends MapGenStructure {
    
    private static MapGenOutpost INSTANCE;
    private final IChunkGenerator generator;
    
    public MapGenOutpost(IChunkGenerator generator) {
        this.generator = generator;
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
        if (!world.getBiomeProvider().areBiomesViable(pos.getX(), pos.getZ(), 0, OutpostConfig.getSpawnBiomes())) return false;
        BlockPos village = world.findNearestStructure("Village", pos, true);
        if (village == null) return true;
        return village.distanceSq(pos) >= (OutpostConfig.distanceFromVillage * OutpostConfig.distanceFromVillage);
    }
    
    @Override
    public OutpostStart getStructureAt(BlockPos pos) {
        for (StructureStart structure : structureMap.values()) if (((OutpostStart)structure).isInStructure(pos)) return (OutpostStart) structure;
        return null;
    }
    
    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new OutpostStart(world, rand, chunkX, chunkZ, generator);
    }
    
    public static MapGenOutpost getInstance(IChunkGenerator generator) {
        if (INSTANCE == null) INSTANCE = new MapGenOutpost(generator);
        if (INSTANCE.generator != generator) INSTANCE = new MapGenOutpost(generator);
        return INSTANCE;
    }
    
    public static class OutpostStart extends StructureStart {
    
        private final BlockPos center;
        
        public OutpostStart(World world, Random rand, int chunkX, int chunkZ, IChunkGenerator generator) {
            int x = chunkX << 4;
            int z = chunkZ << 4;
            ChunkPrimer chunkprimer = new ChunkPrimer();
            if (generator instanceof ChunkGeneratorOverworld) ((ChunkGeneratorOverworld)generator).setBlocksInChunk(chunkX, chunkZ, chunkprimer);
            int y = generator instanceof ChunkGeneratorFlat ? ((ChunkGeneratorFlat)generator).flatWorldGenInfo.getFlatLayers().size() : getY(1, 1, 13, 13, chunkprimer);
            center = new BlockPos(x + 8, y + 16, z + 8);
            BlockPos pos = new BlockPos(x, y, z);
            RaidsLogger.logInfo("Generated outpost at " + pos);
            components.addAll(StructureOutpostPieces.watchtower(world.getSaveHandler().getStructureTemplateManager(), pos,
                    Rotation.values()[rand.nextInt(Rotation.values().length)]));
            updateBoundingBox();
        }
        
        private boolean isInStructure(BlockPos pos) {
            boolean b = Math.abs(pos.getX() - center.getX()) < 36 && Math.abs(pos.getY() - center.getY()) < 26 && Math.abs(pos.getZ() - center.getZ()) < 36;
            if (b) RaidsLogger.logInfo(pos + " is in structure at " + center);
            return b;
        }
    
        public int getY(int x, int z, int i, int k, ChunkPrimer primer) {
            int y0 = primer.findGroundBlockIdx(x, z);
            int yj = primer.findGroundBlockIdx(x, z + k);
            int jy = primer.findGroundBlockIdx(x + i, z);
            int j = primer.findGroundBlockIdx(x + i, z + k);
            return Math.min(Math.min(y0, yj), Math.min(jy, j));
        }
    
        public AxisAlignedBB getSpawnBox() {
            return new AxisAlignedBB(center).grow(36, 26, 36);
        }
        
    }
    
}
