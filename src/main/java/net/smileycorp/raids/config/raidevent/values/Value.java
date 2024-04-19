package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;

public interface Value<T extends Comparable<T>> {
    
    T get(RaidContext ctx);
    
    interface Deserializer {
        
        <T extends Comparable<T>> Value<T> apply(JsonObject obj, DataType<T> type);
        
    }
}
