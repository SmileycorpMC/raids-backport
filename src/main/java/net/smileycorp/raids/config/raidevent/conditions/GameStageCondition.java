package net.smileycorp.raids.config.raidevent.conditions;

import com.google.gson.JsonObject;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.values.Value;
import net.smileycorp.raids.config.raidevent.values.ValueRegistry;

public class GameStageCondition implements RaidCondition {

	protected Value<String> stage;

	public GameStageCondition(Value<String> stage) {
		this.stage = stage;
	}

	@Override
	public boolean apply(RaidContext ctx) {
		EntityPlayerMP player = ctx.getPlayer();
		if (player == null) return false;
		return GameStageHelper.hasStage(player, stage.get(ctx));
	}

	public static GameStageCondition deserialize(JsonObject json) {
		try {
			return new GameStageCondition(ValueRegistry.INSTANCE.readValue(DataType.STRING, json.get("value")));
		} catch(Exception e) {
			RaidsLogger.logError("Incorrect parameters for GameStageCondition", e);
		}
		return null;
	}

}
