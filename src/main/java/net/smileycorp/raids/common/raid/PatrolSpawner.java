package net.smileycorp.raids.common.raid;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsLogger;
import net.smileycorp.raids.common.entities.EntityPillager;

import java.util.Random;

public class PatrolSpawner {
    
    private int nextTick;
    
    public void tick(WorldServer world) {
        if (world == null || world.getDifficulty() == EnumDifficulty.PEACEFUL) return;
        Random rand = world.rand;
        if (nextTick-- > 0) return;
        nextTick += 12000 + rand.nextInt(1200);
        long i = world.getWorldTime() / 24000L;
        if ((i < 5 &! world.isDaytime()) || rand.nextInt(5) != 0 || world.playerEntities.isEmpty()) return;
        EntityPlayer player = world.playerEntities.get(rand.nextInt(world.playerEntities.size()));
        if (player.isSpectator() || Raid.isVillage(world, player.getPosition())) return;
        spawnPatrol(world, player.getPosition(), rand);
    }
    
    public void spawnPatrol(WorldServer world, BlockPos source, Random rand) {
        int k = (24 + rand.nextInt(24)) * (rand.nextBoolean() ? -1 : 1);
        int l = (24 + rand.nextInt(24)) * (rand.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(source.add(k, 0, l));
        if (!world.isAreaLoaded(pos.add(-10, 0, -10), pos.add(10, 0, 10))
                || BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MUSHROOM)) return;
        int k1 = (int) Math.ceil(world.getDifficultyForLocation(pos).getAdditionalDifficulty()) + 1;
        RaidsLogger.logInfo("Spawning patrol with " + (k1 - 1) + " members at " + pos);
        for (int l1 = 0; l1 < k1; l1++) {
            pos.setY(world.getHeight(pos.getX(), pos.getZ()));
            if (l1 == 0) if (!spawnPatrolMember(world, pos, true)) break;
            else spawnPatrolMember(world, pos, false);
            pos = pos.setPos(pos.getX() + rand.nextInt(5) - rand.nextInt(5), pos.getY(), pos.getZ() + rand.nextInt(5) - rand.nextInt(5));
        }
    }
    
    private boolean spawnPatrolMember(WorldServer world, BlockPos pos, boolean leader) {
        IBlockState state = world.getBlockState(pos);
        if (state.isFullBlock() || state.getBlock() instanceof BlockLiquid || world.getLightBrightness(pos) > 8)
            return false;
        EntityPillager pillager = new EntityPillager(world);
        pillager.setPosition(pos.getX(), pos.getY(), pos.getZ());
        pillager.onInitialSpawn(world.getDifficultyForLocation(pos), null);
        pillager.setGlowing(true);
        if (pillager.hasCapability(RaidsContent.RAIDER, null)) {
            Raider raider = pillager.getCapability(RaidsContent.RAIDER, null);
            raider.findPatrolTarget();
            if (leader) raider.setLeader();
        }
        RaidsLogger.logInfo("Spawning patrol member " + pillager + " at " + pos);
        return world.spawnEntity(pillager);
    }
}
