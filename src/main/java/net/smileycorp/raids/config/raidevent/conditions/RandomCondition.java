package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class RandomCondition implements RaidCondition {

	protected Value<Double> chance;

	public RandomCondition(Value<Double> chance) {
		this.chance = chance;
	}

	@Override
	public boolean apply(RaidContext ctx) {
		return ctx.getRand().nextFloat() <= chance.get(ctx);
	}

	public static RandomCondition deserialize(JsonElement json) {
		try {
			return new RandomCondition(ValueRegistry.INSTANCE.readValue(DataType.DOUBLE, json));
		} catch(Exception e) {
			RaidsLogger.logError("Incorrect parameters for RandomCondition", e);
		}
		return null;
	}

}
