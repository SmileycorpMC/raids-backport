package net.smileycorp.raids.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.client.ClientHandler;

public class RaidSoundMessage implements IMessage {
	
	private BlockPos pos;
	
	public RaidSoundMessage() {}
	
	public RaidSoundMessage(BlockPos pos) {
		this.pos = pos;
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
    
    public IMessage process(MessageContext ctx) {
		if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> ClientHandler.playRaidSound(pos));
		return null;
    }
	
}
