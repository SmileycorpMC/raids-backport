package net.smileycorp.raids.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.WorldDataRaids;
import net.smileycorp.raids.config.RaidConfig;

public class BadOmenPotion extends RaidsPotion {
    
    private static final ResourceLocation OMINOUS_TEXTURE = Constants.loc("textures/mob_effect/bad_omen_121.png");
    
    public BadOmenPotion() {
        super(true, 0x0B6138, "bad_omen");
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
            if (!Raid.isVillage(world, player.getPosition())) return;
            if (RaidConfig.ominousBottles) {
                Raid raid = WorldDataRaids.getData((WorldServer) world).getRaidAt(player.getPosition());
                if (raid == null || raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
                    if (RaidConfig.raidCenteredOnPlayer) RaidOmenTracker.setRaidStart(player);
                    player.addPotionEffect(new PotionEffect(RaidsContent.RAID_OMEN, 600, amplifier));
                    player.removePotionEffect(RaidsContent.BAD_OMEN);
                }
            }
            else WorldDataRaids.getData((WorldServer) world).createOrExtendRaid(player);
        }
    }
    
    protected ResourceLocation getTexture() {
        return RaidConfig.ominousBottles ? OMINOUS_TEXTURE : super.getTexture();
    }
    
}
