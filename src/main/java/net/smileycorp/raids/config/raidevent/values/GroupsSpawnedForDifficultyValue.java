package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.EnumDifficulty;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class GroupsSpawnedForDifficultyValue implements Value<Integer> {
    
    protected Value<?> difficulty;
    
    public GroupsSpawnedForDifficultyValue(Value<?> difficulty) {
        this.difficulty = difficulty;
    }
    
    @Override
    public Integer get(RaidContext ctx) {
        Comparable value = difficulty.get(ctx);
        EnumDifficulty difficulty = (value instanceof String ? EnumDifficulty.valueOf((String) value) : EnumDifficulty.getDifficultyEnum((Integer) value));
        Raid raid = ctx.getRaid();
       return raid == null || difficulty == null ? 0 : raid.getNumGroups(difficulty);
    }
    
    public static <T extends Comparable<T>> Value<T> deserialize(JsonObject object, DataType<T> type) {
        try {
            if (type != DataType.INT) throw new ClassCastException();
            Value value;
            JsonElement element = object.get("value");
            try {
                value = ValueRegistry.INSTANCE.readValue(DataType.STRING, element);
            } catch (Exception e) {
                value = ValueRegistry.INSTANCE.readValue(DataType.INT, element);
            }
            return (Value<T>) new GroupsSpawnedForDifficultyValue(value);
        } catch (Exception e) {
            RaidsLogger.logError("invalid value for GroupsSpawnedForDifficultyValue", e);
        }
        return null;
    }
    
    
}
