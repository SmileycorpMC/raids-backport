package net.smileycorp.raids.common.raid.data;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class RaidSpawnTable {
    
    private final List<RaidEntry> entries = Lists.newArrayList();
    private final List<Condition> conditions = Lists.newArrayList();
    
    private RaidSpawnTable(List<RaidEntry> entries, List<Condition> conditions) {
        this.entries.addAll(entries);
        this.conditions.addAll(conditions);
    }
    
    public boolean shouldApply(World level, EntityPlayer player, Random rand) {
        for (Condition condition : conditions) if (!condition.apply(level, player, rand)) return false;
        return true;
    }
    
    
}
