package net.smileycorp.raids.config.raidevent.values;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.BinaryOperation;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.data.UnaryOperation;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.util.Map;

public class ValueRegistry {

    public static final ValueRegistry INSTANCE = new ValueRegistry();
    
    private final Map<String, Value.Deserializer> values;
    
    private ValueRegistry() {
        values = Maps.newHashMap();
    }
    
    public <T extends Comparable<T>> void registerValue(String name, Value.Deserializer value) {
        if (!values.containsKey(name)) {
            values.put(name, value);
            RaidsLogger.logInfo("Registered value " + name);
        }
    }
    
    public <T extends Comparable<T>> Value<T> readValue(DataType<T> type, JsonElement json) {
        if (json.isJsonPrimitive()) return new StaticValue(type.readFromJson(json));
        if (!json.isJsonObject()) return null;
        JsonObject obj = json.getAsJsonObject();
        try {
            return values.get(obj.get("name").getAsString()).apply(obj, type);
        } catch (Exception e) {
            RaidsLogger.logError("Failed reading condition " + obj, e);
        }
        return new EmptyValue<>(type);
    }
    
    public void registerDefaultValues() {
        UnaryOperation.values().forEach(operation -> registerValue(operation.getName(), UnaryOperationValue.of(operation)::deserialize));
        BinaryOperation.values().forEach(operation -> registerValue(operation.getName(), BinaryOperationValue.of(operation)::deserialize));
        registerValue("conditional", ConditionalValue::deserialize);
        registerValue("weighted_random", WeightedRandomValue::deserialize);
        registerValue("level_nbt", LevelNBTValue::deserialize);
        registerValue("player_nbt", PlayerNBTValue::deserialize);
        registerValue("wave", WaveValue::deserialize);
        registerValue("number_spawned", NumberSpawnedValue::deserialize);
        registerValue("groups_spawned", GroupsSpawnedValue::deserialize);
        registerValue("groups_for_difficulty", GroupsSpawnedForDifficultyValue::deserialize);
    }
    
}
