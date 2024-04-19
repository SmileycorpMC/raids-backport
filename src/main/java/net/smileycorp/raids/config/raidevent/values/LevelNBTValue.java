package net.smileycorp.raids.config.raidevent.values;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smileycorp.raids.common.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;

public class LevelNBTValue<T extends Comparable<T>> extends NBTValue<T> {

	private LevelNBTValue(Value<String> value, DataType<T> type) {
		super(value, type);
	}

	@Override
	protected NBTTagCompound getNBT(RaidContext ctx)  {
		WorldInfo info = ctx.getWorld().getWorldInfo();
		NBTTagCompound nbt = info.cloneNBTCompound(new NBTTagCompound());
		FMLCommonHandler.instance().handleWorldDataSave((SaveHandler) ctx.getWorld().getSaveHandler(), info, nbt);
		return nbt;
	}
	
	public static <T extends Comparable<T>> LevelNBTValue<T> deserialize(JsonObject object, DataType<T> type) {
		try {
			if (object.has("value")) return new LevelNBTValue(ValueRegistry.INSTANCE.readValue(DataType.STRING, object.get("value")), type);
		} catch (Exception e) {
			RaidsLogger.logError("invalid value for LevelNBTValue", e);
		}
		return null;
	}
	
}
