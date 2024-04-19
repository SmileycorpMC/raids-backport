package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonElement;
import net.minecraft.world.EnumDifficulty;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class GameDifficultyCondition implements RaidCondition {
    
    protected Value<?> difficulty;
    
    public GameDifficultyCondition(Value<?> difficulty) {
        this.difficulty = difficulty;
    }
    
    @Override
    public boolean apply(RaidContext ctx) {
        Comparable value = difficulty.get(ctx);
        return ctx.getDifficulty() == (value instanceof String ? EnumDifficulty.valueOf((String) value) : EnumDifficulty.getDifficultyEnum((Integer) value));
    }
    
    public static GameDifficultyCondition deserialize(JsonElement json) {
        try {
            Value getter;
            try {
                getter = ValueRegistry.INSTANCE.readValue(DataType.STRING, json);
            } catch (Exception e) {
                getter = ValueRegistry.INSTANCE.readValue(DataType.INT, json);
            }
            return new GameDifficultyCondition(getter);
        } catch(Exception e) {
            RaidsLogger.logError("Incorrect parameters for GameDifficultyCondition", e);
        }
        return null;
    }
    
}
