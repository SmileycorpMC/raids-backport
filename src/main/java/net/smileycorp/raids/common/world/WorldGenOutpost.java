package net.smileycorp.raids.common.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.OutpostConfig;

import java.util.Random;

public class WorldGenOutpost implements IWorldGenerator {
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!canSpawnStructureAtCoords(world, chunkX, chunkZ)) return;
        OutpostStart outpost = new OutpostStart(world, world.rand, chunkX, chunkZ, ((ChunkProviderServer)world.getChunkProvider()).chunkGenerator);
        AxisAlignedBB box = outpost.getSpawnBox();
        outpost.generateStructure(world, world.rand, new StructureBoundingBox((int) box.minX, (int) box.minZ, (int) box.maxX, (int) box.maxZ));
        WorldDataOutposts.getData((WorldServer) world).addOutpost(outpost);
    }
    
    protected boolean canSpawnStructureAtCoords(World world, int chunkX, int chunkZ) {
        if (world.provider.getDimension() != 0) return false;
        int x = chunkX;
        int z = chunkZ;
        if (x < 0) x -= OutpostConfig.maxDistance - 1;
        if (z < 0) z -= OutpostConfig.maxDistance - 1;
        int dx = (x / OutpostConfig.maxDistance);
        int dz = (z / OutpostConfig.maxDistance);
        Random random = world.setRandomSeed(dx, dz, 165745296);
        dx = dx * OutpostConfig.maxDistance + random.nextInt(OutpostConfig.maxDistance - OutpostConfig.maxDistance / 4);
        dz = dz * OutpostConfig.maxDistance + random.nextInt(OutpostConfig.maxDistance - OutpostConfig.maxDistance / 4);
        if (chunkX != dx || chunkZ != dz) return false;
        BlockPos pos = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
        if (!world.getBiomeProvider().areBiomesViable(pos.getX(), pos.getZ(), 1, OutpostConfig.getGenerationBiomes())) return false;
        BlockPos village = world.findNearestStructure("Village", pos, true);
        if (village == null) return true;
        return village.distanceSq(pos) > (OutpostConfig.distanceFromVillage * OutpostConfig.distanceFromVillage);
    }
    
    public static class OutpostStart extends StructureStart {
    
        private BlockPos center;
        
        public OutpostStart(NBTTagCompound nbt) {
            readFromNBT(nbt);
        }
        
        public OutpostStart(World world, Random rand, int chunkX, int chunkZ, IChunkGenerator generator) {
            int x = chunkX << 4;
            int z = chunkZ << 4;
            int y =  world.getHeight(x + 8, z + 8);
            center = new BlockPos(x + 8, y, z + 8);
            RaidsLogger.logInfo("Generated outpost at " + center);
            components.addAll(StructureOutpostPieces.watchtower(world.getSaveHandler().getStructureTemplateManager(), center,
                    Rotation.values()[rand.nextInt(Rotation.values().length)]));
            updateBoundingBox();
        }
        
        public BlockPos getCenter() {
            return center;
        }
        
        public boolean isInStructure(BlockPos pos) {
            if (center == null) return false;
            return Math.abs(pos.getX() - center.getX()) < 36 && Math.abs(pos.getY() - center.getY()) < 26 && Math.abs(pos.getZ() - center.getZ()) < 36;
        }
    
        public AxisAlignedBB getSpawnBox() {
            if (center == null) return null;
            return new AxisAlignedBB(center).grow(36, 26, 36);
        }
    
        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            if (center == null) return;
            nbt.setInteger("X", center.getX());
            nbt.setInteger("Y", center.getY());
            nbt.setInteger("Z", center.getZ());
        }
    
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            if (!nbt.hasKey("X")) return;
            center = NBTUtil.getPosFromTag(nbt);
        }
        
    }
    
}
