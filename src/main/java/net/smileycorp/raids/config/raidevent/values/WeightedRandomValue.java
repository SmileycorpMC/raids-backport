package net.smileycorp.raids.config.raidevent.values;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.common.util.WeightedOutputs;

import java.util.Map;

public class WeightedRandomValue<T extends Comparable<T>> implements Value<T> {
    
    private final WeightedOutputs<Value<T>> outputs;
    
    public WeightedRandomValue(WeightedOutputs<Value<T>> outputs) {
        this.outputs = outputs;
    }
    
    @Override
    public T get(RaidContext ctx) {
        return outputs.getResult(ctx.getRand()).get(ctx);
    }
    
    public static <T extends Comparable<T>> WeightedRandomValue deserialize(JsonObject json, DataType<T> type) {
        Map<Value<T>, Integer> values = Maps.newHashMap();
        try {
            if (type.isNumber()) throw new ClassCastException();
            for (JsonElement element : json.get("value").getAsJsonArray()) {
                try {
                    JsonObject entry = element.getAsJsonObject();
                    Value<T> getter = ValueRegistry.INSTANCE.readValue(type, entry.get("value"));
                    if (getter != null) values.put(getter, entry.get("weight").getAsInt());
                } catch (Exception e) {
                    RaidsLogger.logError("invalid entry for " + element + " for WeightedRandomValue", e);
                }
            }
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for WeightedRandomValue", e);
        }
        return new WeightedRandomValue(new WeightedOutputs(values));
    }
    
}
