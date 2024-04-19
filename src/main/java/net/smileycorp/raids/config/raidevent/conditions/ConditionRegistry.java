package net.smileycorp.raids.config.raidevent.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.raids.common.data.LogicalOperation;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.util.Map;
import java.util.function.Function;

public class ConditionRegistry {

    public static final ConditionRegistry INSTANCE = new ConditionRegistry();
    
    private final Map<String, Function<JsonElement, RaidCondition>> conditions;
    
    private ConditionRegistry() {
        conditions = Maps.newHashMap();
    }
    
    public void registerCondition(String name, Function<JsonElement, RaidCondition> condition) {
        if (!conditions.containsKey(name)) {
            RaidsLogger.logInfo("Registered condition " + name);
            conditions.put(name, condition);
        }
    }
    
    public RaidCondition readCondition(JsonObject json) {
        try {
            return conditions.get(json.get("name").getAsString()).apply(json.get("value"));
        } catch (Exception e) {
            RaidsLogger.logError("Failed reading condition " + json, e);
        }
        return null;
    }
    
    public void registerDefaultConditions() {
        for (LogicalOperation operation : LogicalOperation.values())
            registerCondition(operation.getName(), e -> LogicalCondition.deserialize(operation, e));
        registerCondition("not", NotCondition::deserialize);
        registerCondition("comparison", ComparisonCondition::deserialize);
        registerCondition("random", RandomCondition::deserialize);
        registerCondition("biome", BiomeCondition::deserialize);
        registerCondition("local_difficulty", LocalDifficultyCondition::deserialize);
        registerCondition("game_difficulty", GameDifficultyCondition::deserialize);
        registerCondition("advancement", AdvancementCondition::deserialize);
        registerCondition("is_bonus", IsBonusCondition::deserialize);
        if (Loader.isModLoaded("gamestages")) registerCondition("gamestage", GameStageCondition::deserialize);
    }
    
}
