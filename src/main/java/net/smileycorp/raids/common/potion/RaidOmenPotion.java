package net.smileycorp.raids.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidsParticleMessage;
import net.smileycorp.raids.common.util.EnumRaidsParticle;

import java.util.Random;

public class RaidOmenPotion extends RaidsPotion {
    
    private static final ResourceLocation ADVANCED_TEXTURE = Constants.loc("textures/mob_effect/advanced_raid_omen.png");
    
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
        if (rand.nextBoolean()) {
            if (amplifier > 4) entity.world.spawnParticle(EnumParticleTypes.FLAME, entity.posX + (rand.nextDouble() - 0.5D) * (double)entity.width,
                    entity.posY + rand.nextDouble() * (double)entity.height, entity.posZ + (rand.nextDouble() - 0.5D) * (double)entity.width,
                    0 ,0, 0);
            return;
        }
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RaidsParticleMessage(EnumRaidsParticle.RAID_OMEN,
                        entity.posX + (rand.nextDouble() - 0.5D) * (double)entity.width, entity.posY + rand.nextDouble() * (double)entity.height,
                        entity.posZ + (rand.nextDouble() - 0.5D) * (double)entity.width, (double) (amplifier > 4 ? 0xF75E00 : 0xDE4058)),
                new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 32));
    }
    
    protected ResourceLocation getTexture(PotionEffect effect) {
        return effect.getAmplifier() > 4 ? ADVANCED_TEXTURE : super.getTexture(effect);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    protected void renderEffect(PotionEffect effect, int x, int y, float alpha) {
        if (effect.getAmplifier() > 4) ClientHandler.renderFire(x + 1, y - 4, alpha);
        super.renderEffect(effect, x, y, alpha);
    }
    
}
