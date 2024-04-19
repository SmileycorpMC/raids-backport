package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class GroupsSpawnedValue implements Value<Integer> {
    
    @Override
    public Integer get(RaidContext ctx) {
        Raid raid = ctx.getRaid();
       return raid == null ? 0 : raid.getNumGroups();
    }
    
    public static <T extends Comparable<T>> Value<T> deserialize(JsonObject object, DataType<T> type) {
        try {
            if (type != DataType.INT) throw new ClassCastException();
            return (Value<T>) new GroupsSpawnedValue();
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for GroupsSpawnedValue", e);
        }
        return null;
    }
    
    
}
