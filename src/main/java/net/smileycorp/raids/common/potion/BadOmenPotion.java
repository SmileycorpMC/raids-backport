package net.smileycorp.raids.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.WorldDataRaids;

public class BadOmenPotion extends RaidsPotion{
    
    public BadOmenPotion() {
        super(true, 0x0b6138, "bad_omen");
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayerMP && !((EntityPlayerMP)entity).isSpectator()) {
            EntityPlayerMP player = (EntityPlayerMP)entity;
            World world = player.world;
            if (world.getDifficulty() == EnumDifficulty.PEACEFUL) return;
            if (Raid.isVillage(world, player.getPosition())) WorldDataRaids.getData((WorldServer) world).createOrExtendRaid(player);
        }
    }
    
}
