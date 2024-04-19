package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class RaidContext {
    
    private final WorldServer world;
    private final Random rand;
    private final Raid raid;
    private final BlockPos pos;
    private final EntityPlayerMP player;
    private final int wave;
    private final boolean isBonusWave;
    private final Map<Class<? extends EntityLiving>, Integer> numSpawned;
    
    private RaidContext(Builder builder) {
        world = builder.world;
        rand = builder.rand;
        raid = builder.raid;
        pos = builder.pos;
        player = builder.player;
        wave = builder.wave;
        isBonusWave = builder.isBonusWave;
        numSpawned = builder.numSpawned;
    }
    
    public WorldServer getWorld() {
        return world;
    }
    
    public Random getRand() {
        return rand;
    }
    
    public Raid getRaid() {
        return raid;
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    public EntityPlayerMP getPlayer() {
        return player;
    }
    
    public int getWave() {
        return wave;
    }
    
    public boolean isBonusWave() {
        return isBonusWave;
    }
    
    public int getNumSpawned(Class entity) {
        return numSpawned.containsKey(entity) ? numSpawned.get(entity) : 0;
    }
    
    public EnumDifficulty getDifficulty() {
        return world.getDifficulty();
    }
    
    
    public static class Builder {
    
        private final WorldServer world;
        private final Random rand;
        private final Raid raid;
        private BlockPos pos;
        private EntityPlayerMP player;
        private int wave;
        private boolean isBonusWave;
        private Map<Class<? extends EntityLiving>, Integer> numSpawned;
    
        private Builder(WorldServer world, Random rand, @Nullable Raid raid) {
            this.world =  world;
            this.rand = rand;
            this.raid = raid;
        }
        
        public static Builder of(WorldServer world, Random rand) {
            return new Builder(world, rand, null);
        }
    
        public static Builder of(Raid raid) {
            return new Builder(raid.getWorld(), raid.getRandom(), raid);
        }
        
        public Builder pos(BlockPos pos) {
            this.pos = pos;
            return this;
        }
    
        public Builder player(EntityPlayerMP player) {
            this.player = player;
            return this;
        }
    
        public Builder wave(int wave) {
            this.wave = wave;
            return this;
        }
    
        public Builder bonus(boolean isBonusWave) {
            this.isBonusWave = isBonusWave;
            return this;
        }
    
        public Builder spawned(Map<Class<? extends EntityLiving>, Integer> numSpawned) {
            this.numSpawned = numSpawned;
            return this;
        }
        
        public RaidContext build() {
            return new RaidContext(this);
        }
    
    }
    
}
