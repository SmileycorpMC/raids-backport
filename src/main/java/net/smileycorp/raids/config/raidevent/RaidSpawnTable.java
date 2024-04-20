package net.smileycorp.raids.config.raidevent;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.conditions.RaidCondition;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RaidSpawnTable {
    
    private final String name;
    private final List<RaidEntry> entries = Lists.newArrayList();
    private final List<RaidCondition> conditions = Lists.newArrayList();
    private final SoundEvent sound;
    
    RaidSpawnTable(String name, List<RaidEntry> entries, List<RaidCondition> conditions, SoundEvent sound) {
        this.name = name;
        this.entries.addAll(entries);
        this.conditions.addAll(conditions);
        this.sound = sound == null ? sound : RaidsSoundEvents.RAID_HORN;
    }
    
    public boolean shouldApply(RaidContext ctx) {
        for (RaidCondition condition : conditions) if (!condition.apply(ctx)) {
            RaidsLogger.logInfo("Table " + name + " failed on condition " + condition);
            return false;
        }
        return true;
    }
    
    public void addEntry(RaidEntry entry) {
        entries.add(entry);
    }
    
    public void removeEntries(Class<? extends EntityLiving> entity) {
        entries.removeIf(entry -> entry.getEntity().getEntityClass() == entity);
    }
    
    public void removeEntry(Class<? extends EntityLiving> entity, int index) {
        int found = 0;
        Iterator<RaidEntry> iterator = entries.listIterator();
        while (iterator.hasNext()) {
            RaidEntry entry = iterator.next();
            if (entry.getEntity().getEntityClass() == entity) {
                if (found == index) {
                    entries.remove(entry);
                    return;
                }
                found++;
            }
        }
    }
    
    public List<EntityLiving> getWaveEntities(Raid raid, BlockPos pos, int wave, boolean isBonusWave) {
        List<EntityLiving> entities = Lists.newArrayList();
        for (RaidEntry entry : entries) {
            for (int i = 0; i < entry.getCount(raid, wave, pos, isBonusWave); i++) {
                try {
                    Random rand = raid.getRandom();
                    entry.spawnEntity(raid, wave, raid.getWorld().getHeight(pos.north(rand.nextInt(6)-3).east(rand.nextInt(6)-3)), entities, isBonusWave);
                } catch (Exception e) {
                    RaidsLogger.logError("Could not spawn entity for entry " + entry, e);
                }
            }
        }
        return entities;
    }
    
    public SoundEvent getSound() {
        return sound;
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
    
    public String getName() {
        return name;
    }
    
}
