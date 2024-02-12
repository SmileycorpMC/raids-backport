package net.smileycorp.raids.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.item.ItemCrossbow;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHandler {
	
	@SubscribeEvent
	public static void renderHand(RenderSpecificHandEvent event) {
		EnumHand hand = event.getHand();
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null) return;
		if (hand == EnumHand.MAIN_HAND) {
			if (player.getActiveHand() != EnumHand.OFF_HAND) return;
			if (player.getActiveItemStack().getItem() == RaidsContent.CROSSBOW) event.setCanceled(true);
		}
		else if (hand == EnumHand.OFF_HAND) {
			if (player.getActiveHand() == EnumHand.MAIN_HAND && player.getActiveItemStack().getItem() == RaidsContent.CROSSBOW) event.setCanceled(true);
			ItemStack main = player.getHeldItemMainhand();
			if (main.getItem() == RaidsContent.CROSSBOW && ItemCrossbow.isCharged(main)) event.setCanceled(true);
		}
	}

	public static void playRaidSound(BlockPos pos) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		EntityPlayer player = mc.player;
		Vec3d dir = DirectionUtils.getDirectionVecXZ(player.getPosition(), pos);
		BlockPos soundPos = new BlockPos(player.posX + (13*dir.x), player.posY, player.posZ + (13*dir.z));
		float pitch = 1+((world.rand.nextInt(6)-3)/10);
		world.playSound(player, soundPos, RaidsSoundEvents.RAID_HORN, SoundCategory.HOSTILE, 0.3f, pitch);
	}

}
