package net.smileycorp.raids.config.raidevent.conditions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface TableCondition {
    
    boolean apply(World level, BlockPos pos, EntityPlayer player, Random rand);
    
}
