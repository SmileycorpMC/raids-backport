package net.smileycorp.raids.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidsParticleMessage;
import net.smileycorp.raids.common.util.EnumRaidsParticle;

import java.util.Random;

public class RaidOmenPotion extends RaidsPotion {
    
    public RaidOmenPotion() {
        super(true, 0xDE4058, "raid_omen");
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 3 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        Random rand = entity.getRNG();
        if (entity.isInvisible()) return;
        if (rand.nextBoolean()) return;
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RaidsParticleMessage(EnumRaidsParticle.RAID_OMEN, entity.posX + (rand.nextDouble() - 0.5D) * (double)entity.width,
                entity.posY + rand.nextDouble() * (double)entity.height, entity.posZ + (rand.nextDouble() - 0.5D) * (double)entity.width, (double) 0xDE4058),
                new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 32));
    }
    
}
