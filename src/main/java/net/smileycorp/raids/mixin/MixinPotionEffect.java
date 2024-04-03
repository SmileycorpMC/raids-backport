package net.smileycorp.raids.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.WorldDataRaids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEffect.class)
public class MixinPotionEffect {
    
    @Shadow @Final private Potion potion;
    
    @Shadow private int duration;
    
    @Inject(at =@At("HEAD"), method = "performEffect")
    public void init(EntityLivingBase entity, CallbackInfo callback)  {
        if (potion == RaidsContent.RAID_OMEN && duration == 0) {
            if (entity instanceof EntityPlayerMP && !((EntityPlayerMP)entity).isSpectator()) {
                EntityPlayerMP player = (EntityPlayerMP)entity;
                World world = player.world;
                if (world.getDifficulty() == EnumDifficulty.PEACEFUL) return;
                if (Raid.isVillage(world, player.getPosition())) WorldDataRaids.getData((WorldServer) world).createOrExtendRaid(player);
            }
        }
    }
    
}
