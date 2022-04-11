package net.smileycorp.raids.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RemoveEffectMessage implements IMessage {
	
	private BlockPos pos;
	
	public RemoveEffectMessage() {}
	
	public RemoveEffectMessage(BlockPos pos) {
		this.pos=pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		double x = buf.readDouble();
		double z = buf.readDouble();
		pos = new BlockPos(x, 0, z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (pos!=null) {
			buf.writeDouble(pos.getX());
			buf.writeDouble(pos.getZ());
		}
	}
	
	
	public BlockPos getPos() {
		return pos;
	}
}
