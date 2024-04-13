package net.smileycorp.raids.common.raid.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Random;

public interface Condition {
    
    boolean apply(World level, EntityPlayer player, Random rand);
    
}
