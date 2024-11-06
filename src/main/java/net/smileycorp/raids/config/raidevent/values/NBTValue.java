package net.smileycorp.raids.config.raidevent.values;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;

public abstract class NBTValue<T extends Comparable<T>> implements Value<T> {

	protected final Value<String> value;
	private final DataType<T> type;
	
	public NBTValue(Value<String> value, DataType<T> type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public T get(RaidContext ctx) {
		try {
			findValue(type, value.get(ctx), getNBT(ctx));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public T findValue(DataType<T> type, String target, NBTTagCompound compound) throws Exception {
		String[] directory = target.split("\\.");
		NBTBase nbt = compound;
		for (int i = 0; i < directory.length; i++) {
			try {
				if (nbt instanceof NBTTagCompound) {
					if (i == directory.length - 1) {
						T value = type.readFromNBT((NBTTagCompound) nbt, directory[i]);
						if (value == null) throw new Exception("Value " + directory[i] + "is not of type " + type.getType());
						return value;
					} else nbt = ((NBTTagCompound) nbt).getTag(directory[i]);
				} else if (nbt instanceof NBTTagList) {
					NBTBase nextNBT = null;
					for (NBTBase tag : ((NBTTagList)nbt)) if (tag instanceof NBTTagCompound && ((NBTTagCompound) tag).getString("Name").equals(directory[i])) {
						nextNBT = tag;
						break;
					}
					if  (nextNBT == null) throw new Exception("NBTTagList " + nbt + " does not contain \"Name\":\"" + directory[i] + "\"");
					else nbt = nextNBT;
				} else throw new Exception("Value " + directory[i] + " is not an applicable type or cannot be found as nbt is " + nbt);
			} catch (Exception e) {
				StringBuilder builder = new StringBuilder();
				for (int j = 0; j < directory.length; j++) {
					if (i == j) builder.append(">"+directory[j]+"<");
					else builder.append(directory[j]);
					if (j < directory.length - 1) builder.append("\\.");
				}
				throw new Exception(builder + " " + e.getMessage(), e.getCause());
			}
		}
		throw new Exception("Could not find value " + directory);
	}

	protected abstract NBTTagCompound getNBT(RaidContext ctx);

}
