package net.smileycorp.raids.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.common.Constants;

public class PacketHandler {

	public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

	public static void initPackets() {
		NETWORK_INSTANCE.registerMessage(RaidSoundMessage::process, RaidSoundMessage.class, 0, Side.CLIENT);
		NETWORK_INSTANCE.registerMessage(RaidsParticleMessage::process, RaidsParticleMessage.class, 1, Side.CLIENT);
	}

}
