package net.smileycorp.raids.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.util.EnumRaidsParticle;

public class RaidsParticleMessage implements IMessage {
	
	private EnumRaidsParticle type = EnumRaidsParticle.RAID_OMEN;
	private double x, y, z = 0;
	private Double[] data = {};
	
	public RaidsParticleMessage() {}
	
	public RaidsParticleMessage(EnumRaidsParticle type, double x, double y, double z, Double... data) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		type = EnumRaidsParticle.values()[buf.readByte()];
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		data = type.readBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte((byte) type.ordinal());
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		type.writeBytes(buf, data);
	}
	
	public IMessage process(MessageContext ctx) {
		if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> ClientHandler.spawnParticle(type, x, y, z, data));
		return null;
	}
	
}
