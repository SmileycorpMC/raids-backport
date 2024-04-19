package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class NumberSpawnedValue implements Value<Integer> {
    
    private final Value<ResourceLocation> value;
    
    public NumberSpawnedValue(Value<ResourceLocation>value) {
        this.value = value;
    }
    
    @Override
    public Integer get(RaidContext ctx) {
        try {
            return ctx.getNumSpawned(GameData.getEntityRegistry().getValue(value.get(ctx)).getEntityClass());
        } catch (Exception e) {}
        return 0;
    }
    
    public static <T extends Comparable<T>> Value<T> deserialize(JsonObject object, DataType<T> type) {
        try {
            if (type != DataType.INT) throw new ClassCastException();
            if (object.has("value")) return (Value<T>) new NumberSpawnedValue(ValueRegistry.INSTANCE.readValue(DataType.RESOURCE_LOCATION, object.get("value")));
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for NumberSpawnedValue", e);
        }
        return null;
    }
    
    
}
