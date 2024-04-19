package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class NotCondition implements RaidCondition {

	protected RaidCondition condition;

	public NotCondition(RaidCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean apply(RaidContext ctx) {
		return !condition.apply(ctx);
	}

	public static NotCondition deserialize(JsonElement json) {
		try {
			return new NotCondition(ConditionRegistry.INSTANCE.readCondition(json.getAsJsonObject()));
		} catch(Exception e) {
			RaidsLogger.logError("Incorrect parameters for NotCondition", e);
		}
		return null;
	}

}
