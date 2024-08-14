package net.smileycorp.raids.integration.futuremc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.raids.common.Constants;
import thedarkcolour.futuremc.tile.BellTileEntity;

public class FutureMCIntegration {
	
	@CapabilityInject(BellTimer.class)
	public static Capability<BellTimer> BELL_TIMER = null;
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new FutureMCIntegration());
		CapabilityManager.INSTANCE.register(BellTimer.class, new BellTimer.Storage(), () -> new BellTimer.Impl(null));
	}
	
	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity te = event.getObject();
		if (te instanceof BellTileEntity) event.addCapability(Constants.loc("Timer"), new BellTimer.Provider(te));
	}
	
	@SubscribeEvent
	public void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (world.isRemote) return;
		BlockPos pos = event.getPos();
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return;
		if (!tile.hasCapability(BELL_TIMER, null)) return;
		BellTimer timer = tile.getCapability(BELL_TIMER, null);
		if (!timer.isRinging()) timer.setRinging();
	}
	
	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		BellTimer.ACTIVE_BELLS.forEach(BellTimer::updateTimer);
	}
	
}
