package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class AdvancementCondition implements RaidCondition {
    
    
    private final Value<String> advancement;
    
    public AdvancementCondition(Value<String> advancement) {
        this.advancement = advancement;
    }
    
    @Override
    public boolean apply(RaidContext ctx) {
        EntityPlayerMP player = ctx.getPlayer();
        if (player == null) return false;
        return player.getAdvancements().getProgress(player.getServer().getAdvancementManager()
                .getAdvancement(new ResourceLocation(advancement.get(ctx)))).isDone();
    }
    
    public static AdvancementCondition deserialize(JsonObject json) {
        try {
            return new AdvancementCondition(ValueRegistry.INSTANCE.readValue(DataType.STRING, json.get("value")));
        } catch(Exception e) {
            RaidsLogger.logError("Incorrect parameters for AdvancementCondition", e);
        }
        return null;
    }
    
}
