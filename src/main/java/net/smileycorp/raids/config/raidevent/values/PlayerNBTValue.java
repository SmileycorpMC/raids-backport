package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class PlayerNBTValue<T extends Comparable<T>> extends NBTValue<T> {

	private PlayerNBTValue(Value<String> value, DataType<T> type) {
		super(value, type);
	}

	@Override
	protected NBTTagCompound getNBT(RaidContext ctx) {
		EntityPlayerMP player = ctx.getPlayer();
		if (player == null) return new NBTTagCompound();
		return CommandBase.entityToNBT(player);
	}
	
	public static <T extends Comparable<T>> PlayerNBTValue<T> deserialize(JsonObject object, DataType<T> type) {
		try {
			if (object.has("value")) return new PlayerNBTValue<T>(ValueRegistry.INSTANCE.readValue(DataType.STRING, object.get("value")), type);
		} catch (Exception e) {
			RaidsLogger.logError("invalid value for PlayerNBTValue", e);
		}
		return null;
	}

}
