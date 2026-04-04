package net.smileycorp.raids.common.event;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.raids.common.raid.Raid;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CustomRaidEvent extends Event {
    
    private final Raid raid;
    
    protected CustomRaidEvent(Raid raid) {
        this.raid = raid;
    }
    
    public Raid getRaid() {
        return raid;
    }
    
    public World getWorld() {
        return raid.getWorld();
    }
    
    @Nullable
    public EntityPlayer getPlayer() {
        return raid.getCreator();
    }
    
    public String getName() {
        return raid.getRaidDisplayName();
    }
    
    public int getWaves() {
        return raid.getNumGroups();
    }
    
    public BlockPos getPos() {
        return raid.getCenter();
    }
    
    public boolean requireVillage() {
        return raid.requiresVillageCheck();
    }
    
    public List<Class<? extends EntityLiving>> getDetectionWhiteList() {
        return raid.getDetectionWhitelist();
    }
    
    public String getBossBar() {
        return raid.getBossbarTitle();
    }
}
