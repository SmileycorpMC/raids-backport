package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.raid.RaidContext;

public class IsBonusCondition implements RaidCondition {
    
    @Override
    public boolean apply(RaidContext ctx) {
       return ctx.isBonusWave();
    }
    
    public static IsBonusCondition deserialize(JsonObject json) {
        return new IsBonusCondition();
    }
    
}
