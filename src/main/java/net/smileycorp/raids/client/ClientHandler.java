package net.smileycorp.raids.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.network.RemoveEffectMessage;

public class ClientHandler {

	public static void playRaidSound(BlockPos pos) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		EntityPlayer player = mc.player;
		Vec3d dir = DirectionUtils.getDirectionVecXZ(player.getPosition(), pos);
		BlockPos soundPos = new BlockPos(player.posX + (13*dir.x), player.posY, player.posZ + (13*dir.z));
		float pitch = 1+((world.rand.nextInt(6)-3)/10);
		world.playSound(player, soundPos, RaidsContent.RAID_HORN, SoundCategory.HOSTILE, 0.3f, pitch);
	}

	public static void removeEffect(RemoveEffectMessage message) {


	}

}
