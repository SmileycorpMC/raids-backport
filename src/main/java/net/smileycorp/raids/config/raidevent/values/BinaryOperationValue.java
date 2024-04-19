package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.BinaryOperation;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class BinaryOperationValue<T extends Number & Comparable<T>> implements Value<T> {
    
    private final BinaryOperation operation;
    private final Value<T> value1, value2;
    
    private BinaryOperationValue(BinaryOperation operation, Value<T> value1, Value<T> value2) {
        this.operation = operation;
        this.value1 = value1;
        this.value2 = value2;
    }
    
    @Override
    public T get(RaidContext ctx) {
        return (T) operation.apply(value1.get(ctx), value2.get(ctx));
    }
    
    public static Deserializer of(BinaryOperation operation) {
        return new Deserializer(operation);
    }
    
    public static class Deserializer {
        
        private final BinaryOperation operation;
        
        private Deserializer(BinaryOperation operation) {
            this.operation = operation;
        }
    
        public <T extends Comparable<T>> BinaryOperationValue deserialize(JsonObject obj, DataType<T> type) {
            Value<T> getter1 = ValueRegistry.INSTANCE.readValue(type, obj.get("value1"));
            Value<T> getter2 = ValueRegistry.INSTANCE.readValue(type, obj.get("value2"));
            if (getter1 == null || getter2 == null | !type.isNumber()) {
                RaidsLogger.logError("invalid values for BinaryOperationValue " + operation.getName(), new NullPointerException());
                return null;
            }
            return new BinaryOperationValue(operation, getter1, getter2);
        }
        
    }
    
}
