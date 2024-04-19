package net.smileycorp.raids.config.raidevent.conditions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.raids.common.data.LogicalOperation;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.util.List;

public class LogicalCondition implements RaidCondition {

	protected final LogicalOperation operation;
	protected final RaidCondition[] conditions;

	private LogicalCondition(LogicalOperation operation, RaidCondition... conditions) {
		this.operation = operation;
		this.conditions = conditions;
	}

	@Override
	public boolean apply(RaidContext ctx) {
		boolean result = false;
		for (RaidCondition condition : conditions) result = operation.apply(result, condition.apply(ctx));
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < conditions.length; i++) {
			builder.append(conditions[i].toString());
			if (i < conditions.length-1) builder.append(" " + operation.getSymbol() + " ");
		}
		return super.toString() + "[" + builder + "]";
	}

	public static LogicalCondition deserialize(LogicalOperation operation, JsonObject json) {
		try {
			List<RaidCondition> conditions = Lists.newArrayList();
			for (JsonElement element : json.get("value").getAsJsonArray()) {
				try {
					conditions.add(ConditionRegistry.INSTANCE.readCondition(element.getAsJsonObject()));
				} catch(Exception e) {
					RaidsLogger.logError("Failed to read condition of logical " + element, e);
				}
			}
			return new LogicalCondition(operation, conditions.toArray(new RaidCondition[]{}));
		} catch(Exception e) {
			RaidsLogger.logError("Incorrect parameters for LogicalCondition " + operation.getName(), e);
		}
		return null;
	}

}
