package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class WaveValue implements Value<Integer> {
    
    @Override
    public Integer get(RaidContext ctx) {
       return ctx.getWave();
    }
    
    public static <T extends Comparable<T>> Value<T> deserialize(JsonObject object, DataType<T> type) {
        try {
            if (type != DataType.INT) throw new ClassCastException();
            return (Value<T>) new WaveValue();
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for WaveValue", e);
        }
        return null;
    }
    
    
}
