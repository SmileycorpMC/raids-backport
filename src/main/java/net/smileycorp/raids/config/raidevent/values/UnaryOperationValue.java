package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.data.UnaryOperation;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class UnaryOperationValue<T extends Number & Comparable<T>> implements Value<T> {
    
    private final UnaryOperation operation;
    private final Value<T> value;
    
    private UnaryOperationValue(UnaryOperation operation, Value<T> value) {
        this.operation = operation;
        this.value = value;
    }
    
    @Override
    public T get(RaidContext ctx) {
        return (T) operation.apply(value.get(ctx));
    }
    
    public static Deserializer of(UnaryOperation operation) {
        return new Deserializer(operation);
    }
    
    public static class Deserializer {
        
        private final UnaryOperation operation;
        
        private Deserializer(UnaryOperation operation) {
            this.operation = operation;
        }
    
        public <T extends Comparable<T>> UnaryOperationValue deserialize(JsonObject element, DataType<T> type) {
            Value getter = ValueRegistry.INSTANCE.readValue(type, element.get("value"));
            if (getter == null |! type.isNumber()) {
                RaidsLogger.logError("invalid value for UnaryOperationValue " + operation.getName(), new NullPointerException());
                return null;
            }
            return new UnaryOperationValue(operation, getter);
        }
        
    }
    
}
