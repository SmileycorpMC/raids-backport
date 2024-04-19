package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class LocalDifficultyCondition implements RaidCondition {

	protected Value<Double> difficulty;

	public LocalDifficultyCondition(Value<Double> difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public boolean apply(RaidContext ctx) {
		return ctx.getWorld().getDifficultyForLocation(ctx.getPos()).getClampedAdditionalDifficulty() > difficulty.get(ctx);
	}

	public static LocalDifficultyCondition deserialize(JsonObject json) {
		try {
			return new LocalDifficultyCondition(ValueRegistry.INSTANCE.readValue(DataType.DOUBLE, json.get("value")));
		} catch(Exception e) {
			RaidsLogger.logError("Incorrect parameters for LocalDifficultyCondition", e);
		}
		return null;
	}

}
