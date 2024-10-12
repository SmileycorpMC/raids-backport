package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.config.RaidConfig;

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
    private final int omenLevel;
    private final boolean isBonusWave;
    private final Map<Class<? extends EntityLiving>, Integer> numSpawned;
    
    private RaidContext(Builder builder) {
        world = builder.world;
        rand = builder.rand;
        raid = builder.raid;
        pos = builder.pos;
        player = builder.player;
        wave = builder.wave;
        omenLevel = builder.omenLevel;
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
    
    public int getOmenLevel() {
        return omenLevel;
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
        private int omenLevel;
        private boolean isBonusWave;
        private Map<Class<? extends EntityLiving>, Integer> numSpawned;
    
        private Builder(WorldServer world, Random rand, @Nullable Raid raid) {
            this.world =  world;
            this.rand = rand;
            this.raid = raid;
            if (raid != null) omenLevel = raid.getBadOmenLevel();
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
            if (player != null) {
                PotionEffect effect = player.getActivePotionEffect(RaidConfig.ominousBottles ? RaidsContent.RAID_OMEN : RaidsContent.BAD_OMEN);
                if (effect != null) omenLevel = effect.getAmplifier() + 1;
            }
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
