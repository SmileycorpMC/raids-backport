package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class OmenLevelValue implements Value<Integer> {
    
    @Override
    public Integer get(RaidContext ctx) {
        Raid raid = ctx.getRaid();
        return raid == null ? 0 : raid.getBadOmenLevel();
    }
    
    public static <T extends Comparable<T>> Value<T> deserialize(JsonObject object, DataType<T> type) {
        try {
            if (type != DataType.INT) throw new ClassCastException();
            return (Value<T>) new OmenLevelValue();
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for GroupsSpawnedForDifficultyValue", e);
        }
        return null;
    }
    
    
}
