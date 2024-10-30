package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.ComparableOperation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class ComparisonCondition<T extends Comparable<T>> implements RaidCondition {
    
    protected final Value<T> value1;
    protected final ComparableOperation operation;
    protected final Value<T> value2;
    
    private ComparisonCondition(Value<T> value1, ComparableOperation operation, Value<T> value2) {
        this.value1 = value1;
        this.operation = operation;
        this.value2 = value2;
    }
    
    @Override
    public boolean apply(RaidContext ctx) {
        return operation.apply(value1.get(ctx), value2.get(ctx));
    }
    
    public static ComparisonCondition deserialize(JsonObject json) {
        try {
            DataType type = DataType.of(json.get("type").getAsString());
            ComparableOperation operation = ComparableOperation.of(json.get("operation").getAsString());
            Value value1 = ValueRegistry.INSTANCE.readValue(type, json.get("value1"));
            Value value2 = ValueRegistry.INSTANCE.readValue(type, json.get("value2"));
            return new ComparisonCondition(value1, operation, value2);
        } catch(Exception e) {
            RaidsLogger.logError("Incorrect parameters for ComparisonCondition", e);
        }
        return null;
    }
    
}
