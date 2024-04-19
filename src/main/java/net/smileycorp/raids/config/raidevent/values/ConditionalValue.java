package net.smileycorp.raids.config.raidevent.values;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.conditions.ConditionRegistry;
import net.smileycorp.raids.config.raidevent.conditions.RaidCondition;

import java.util.List;

public class ConditionalValue<T extends Comparable<T>> implements Value<T> {
    
    private final Value<T> value, defaultValue;
    private final List<RaidCondition> conditions = Lists.newArrayList();
    
    public ConditionalValue(Value<T> value, Value<T> value2, List<RaidCondition> conditions) {
        this.value = value;
        this.defaultValue = value2;
        this.conditions.addAll(conditions);
    }
    
    @Override
    public T get(RaidContext ctx) {
        for (RaidCondition condition : conditions) if (!condition.apply(ctx)) return defaultValue.get(ctx);
        return value.get(ctx);
    }
    
    public static <T extends Comparable<T>> ConditionalValue deserialize(JsonObject obj, DataType<T> type) {
        Value<T> value = ValueRegistry.INSTANCE.readValue(type, obj.get("value"));
        Value<T> defaultValue = ValueRegistry.INSTANCE.readValue(type, obj.get("default"));
        List<RaidCondition> conditions = Lists.newArrayList();
        if (value == null || defaultValue == null) {
            RaidsLogger.logError("invalid values for ConditionalValue", new NullPointerException());
            return null;
        }
        if (obj.has("conditions")) for (JsonElement element : obj.get("conditions").getAsJsonArray()) {
            RaidCondition condition = ConditionRegistry.INSTANCE.readCondition(element.getAsJsonObject());
            if (condition != null) conditions.add(condition);
        }
        return new ConditionalValue(value, defaultValue, conditions);
    }
    
}
