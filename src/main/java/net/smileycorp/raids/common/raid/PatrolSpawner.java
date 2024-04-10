package net.smileycorp.raids.common.raid;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.PatrolConfig;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class PatrolSpawner {
    
    private int nextTick;
    
    public void tick(WorldServer world) {
        if (world == null || world.getDifficulty() == EnumDifficulty.PEACEFUL) return;
        Random rand = world.rand;
        if (nextTick-- > 0) return;
        nextTick += 12000 + rand.nextInt(1200);
        long i = world.getWorldTime() / 24000;
        if ((i < 5 &! world.isDaytime()) || rand.nextInt(5) != 0 || world.playerEntities.isEmpty()) return;
        EntityPlayer player = world.playerEntities.get(rand.nextInt(world.playerEntities.size()));
        if (player.func_175149_v() || Raid.isVillage(world, player.getPosition())) return;
        spawnPatrol(world, player, rand, false);
    }
    
    public void spawnPatrol(WorldServer world, Entity player, Random rand, boolean isCommand) {
        int k = (24 + rand.nextInt(24)) * (rand.nextBoolean() ? -1 : 1);
        int l = (24 + rand.nextInt(24)) * (rand.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(player.getPosition().add(k, 0, l));
        if (!world.isAreaLoaded(pos.add(-10, 0, -10), pos.add(10, 0, 10))) return;
        if (!isCommand && BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MUSHROOM)) return;
        int numSpawns = (int) Math.ceil(world.getDifficultyForLocation(pos).getAdditionalDifficulty()) + 1;
        RaidsLogger.logInfo("Spawning patrol with " + numSpawns + " members at " + pos);
        if (isCommand && player instanceof EntityPlayer)
            player.sendMessage(new TextComponentTranslation("commands.raids.spawnPatrol.success", numSpawns, pos.getX(), pos.getY(), pos.getZ()));
        List<Class<? extends EntityLiving>> spawns = Lists.newArrayList();
        Map.Entry<Integer, List<Map.Entry<Class<? extends EntityLiving>, Integer>>> entries = PatrolConfig.getSpawnEntities();
        for (int i = 0; i < numSpawns; i++) {
            int c = rand.nextInt(entries.getKey());
            for (Map.Entry<Class<? extends EntityLiving>, Integer> entry : entries.getValue()) {
                if (c < entry.getValue()) spawns.add(entry.getKey());
                break;
            }
        }
        boolean hasLeader = false;
        for (Class<? extends EntityLiving> clazz : spawns) {
            pos.setY(world.getHeight(pos.getX(), pos.getZ()));
            EntityLiving entity = spawnPatrolMember(world, pos, clazz);
            if (!hasLeader && entity != null && entity.hasCapability(RaidsContent.RAIDER, null)) {
                Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
                raider.setLeader();
                hasLeader = true;
            }
            pos = pos.setPos(pos.getX() + rand.nextInt(5) - rand.nextInt(5), pos.getY(), pos.getZ() + rand.nextInt(5) - rand.nextInt(5));
        }
    }
    
    private EntityLiving spawnPatrolMember(WorldServer world, BlockPos pos, Class<? extends EntityLiving> clazz) {
        IBlockState state = world.getBlockState(pos);
        if (state.isFullBlock() || state.getBlock() instanceof BlockLiquid || world.getLightBrightness(pos) > 8)
            return null;
        try {
            EntityLiving entity = clazz.getConstructor(World.class).newInstance(world);
            entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
            entity.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            if (entity.hasCapability(RaidsContent.RAIDER, null)) {
                Raider raider = entity.getCapability(RaidsContent.RAIDER, null);
                raider.findPatrolTarget();
            }
            RaidsLogger.logInfo("Spawning patrol member " + entity + " at " + pos);
            return world.spawnEntity(entity) ? entity : null;
        } catch (Exception e) {
            RaidsLogger.logError("Failed spawning entity from class " + clazz, e);
            return null;
        }
    }
    
}
