package net.smileycorp.raids.mixin;

import net.minecraft.block.BlockEventData;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidsParticleMessage;
import net.smileycorp.raids.common.util.EnumRaidsParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {
    
    protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Inject(at = @At("HEAD"), method = "fireBlockEvent")
    public void raids$fireBlockEvent(BlockEventData event, CallbackInfoReturnable<Boolean> callback) {
        if (event.getBlock() != Blocks.NOTEBLOCK) return;
        Vec3d pos = new Vec3d(event.getPosition()).addVector(0.5f, 0.5f, 0.5f);
        for (EntityAllay allay : getEntities(EntityAllay.class, e -> !e.getHeldItemMainhand().isEmpty() && e.canHearBlock(pos))) {
            allay.setNoteBlockPos(event.getPosition());
            PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RaidsParticleMessage(EnumRaidsParticle.VIBRATION, pos.x, pos.y, pos.z,
                            allay.posX, allay.posY + allay.getEyeHeight(), allay.posZ),
                    new NetworkRegistry.TargetPoint(allay.dimension, pos.x, pos.y, pos.z, 32));
        }
    }
    
}
