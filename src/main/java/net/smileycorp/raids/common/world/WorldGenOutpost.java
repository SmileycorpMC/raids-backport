package net.smileycorp.raids.common.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.OutpostConfig;

import java.util.Random;

public class WorldGenOutpost {

    public static final WorldGenOutpost INSTANCE = new WorldGenOutpost();

    @SubscribeEvent
    public void generate(DecorateBiomeEvent.Pre event) {
        World world = event.getWorld();
        ChunkPos pos = event.getChunkPos();
        if (!canSpawnStructureAtCoords(world, pos.x, pos.z)) return;
        OutpostStart outpost = new OutpostStart(world, world.rand, pos.x, pos.z, ((ChunkProviderServer)world.getChunkProvider()).chunkGenerator);
        WorldDataOutposts.getData((WorldServer) world).addOutpost(outpost);
        AxisAlignedBB box = outpost.getSpawnBox();
        outpost.generateStructure(world, world.rand, new StructureBoundingBox((int) box.minX, (int) box.minZ, (int) box.maxX, (int) box.maxZ));
    }

    @SubscribeEvent
    public void generate(DecorateBiomeEvent.Decorate event) {
        World world = event.getWorld();
        if (WorldDataOutposts.getData((WorldServer) world).isInOutpost(event.getPlacementPos()))
            event.setResult(Event.Result.DENY);
    }
    
    protected boolean canSpawnStructureAtCoords(World world, int chunkX, int chunkZ) {
        if (!OutpostConfig.canGenerateInDimension(world.provider.getDimension())) return false;
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
            RaidsLogger.logInfo("Generated outpost at " + center + " in dimension " + world.provider.getDimension());
            components.addAll(StructureOutpostPieces.watchtower(world.getSaveHandler().getStructureTemplateManager(), center,
                    Rotation.values()[rand.nextInt(Rotation.values().length)]));
            int distance = OutpostConfig.maxDistance - OutpostConfig.featureMinDistance;
            for (int i = 0; i < OutpostConfig.featureCount; i++) {
                if (OutpostConfig.featureChance <= 0) break;
                if (rand.nextFloat() > OutpostConfig.featureChance) continue;
                BlockPos pos = center.add(new BlockPos(DirectionUtils.getRandomDirectionVecXZ(rand)
                        .scale((distance  > 0 ? rand.nextInt(distance) : 0) + OutpostConfig.featureMinDistance)));
                components.addAll(StructureOutpostPieces.feature(rand, world.getSaveHandler().getStructureTemplateManager(), world.getHeight(pos),
                        Rotation.values()[rand.nextInt(Rotation.values().length)]));
            }
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
            return new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                    boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
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
