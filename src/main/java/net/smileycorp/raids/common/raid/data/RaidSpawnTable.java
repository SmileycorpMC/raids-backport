package net.smileycorp.raids.common.raid.data;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class RaidSpawnTable {
    
    private final String name;
    private final List<RaidEntry> entries = Lists.newArrayList();
    private final List<Condition> conditions = Lists.newArrayList();
    
    private RaidSpawnTable(String name, List<RaidEntry> entries, List<Condition> conditions) {
        this.name = name;
        this.entries.addAll(entries);
        this.conditions.addAll(conditions);
    }
    
    public boolean shouldApply(World level, EntityPlayer player, Random rand) {
        for (Condition condition : conditions) if (!condition.apply(level, player, rand)) return false;
        return true;
    }
    
    public int sort(RaidSpawnTable other) {
        String a = name;
        String b = other.name;
        int ia = 0, ib = 0;
        int nza, nzb;
        char ca, cb;
        int result;
        while (true) {
            nza = nzb = 0;
            ca = charAt(a, ia);
            cb = charAt(b, ib);
            while (ca == '0') {
                if (ca == '0') nza++;
                else nza = 0;
                if (!Character.isDigit(charAt(a, ia + 1))) break;
                ca = charAt(a, ia++);
            }
            while (cb == '0') {
                if (cb == '0') nzb++;
                else nzb = 0;
                if (!Character.isDigit(charAt(b, ib + 1))) break;
                cb = charAt(b, ib++);
            }
            if (Character.isDigit(ca) && Character.isDigit(cb))
                if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0) return result;
            if (ca == 0 && cb == 0) return nza - nzb;
            if (ca < cb) return -1;
            else if (ca > cb) return +1;
            ia++;
            ib++;
        }
    }
    
    private char charAt(String s, int i) {
        return i >= s.length() ? 0 : Character.toUpperCase(s.charAt(i));
    }
    
    private int compareRight(String a, String b) {
        int bias = 0;
        int ia = 0;
        int ib = 0;
        for (;; ia++, ib++) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);
            if (!Character.isDigit(ca) && !Character.isDigit(cb)) return bias;
            else if (!Character.isDigit(ca)) return -1;
            else if (!Character.isDigit(cb)) return 1;
            else if (ca < cb) if (bias == 0) bias = -1;
            else if (ca > cb) if (bias == 0) bias = 1;
            else if (ca == 0 && cb == 0) return bias;
        }
    }
    
}
