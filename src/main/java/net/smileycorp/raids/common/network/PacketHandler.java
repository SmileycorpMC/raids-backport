package net.smileycorp.raids.common.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.ModDefinitions;

public class PacketHandler {

	public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModDefinitions.MODID);

	public static void initPackets() {
		NETWORK_INSTANCE.registerMessage(SoundMessageHandler.class, RaidSoundMessage.class, 0, Side.CLIENT);
		NETWORK_INSTANCE.registerMessage(RemoveEffectHandler.class, RemoveEffectMessage.class, 1, Side.CLIENT);
	}

	public static class SoundMessageHandler implements IMessageHandler<RaidSoundMessage, IMessage> {

		public SoundMessageHandler() {}

		@Override
		public IMessage onMessage(RaidSoundMessage message, MessageContext ctx) {

			if (ctx.side == Side.CLIENT) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					ClientHandler.playRaidSound(message.getPos());
				});
			}
			return null;
		}
	}

	public static class RemoveEffectHandler implements IMessageHandler<RemoveEffectMessage, IMessage> {

		public RemoveEffectHandler() {}

		@Override
		public IMessage onMessage(RemoveEffectMessage message, MessageContext ctx) {

			if (ctx.side == Side.CLIENT) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					ClientHandler.removeEffect(message);
				});
			}
			return null;
		}
	}

}
