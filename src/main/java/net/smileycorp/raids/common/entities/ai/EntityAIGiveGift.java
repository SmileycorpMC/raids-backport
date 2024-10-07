package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.util.MathUtils;
import net.smileycorp.raids.config.VillagerGiftsConfig;

public class EntityAIGiveGift  extends EntityAIBase {
    
    private final EntityVillager villager;
    private int cooldown;
    private EntityPlayer hero;
    
    public EntityAIGiveGift(EntityVillager villager) {
        this.villager = villager;
    }
    
    @Override
    public boolean shouldExecute() {
        if (cooldown == 0) return canSeeHero();
        cooldown--;
        return false;
    }
    
    @Override
    public void startExecuting() {
        ItemStack stack = VillagerGiftsConfig.INSTANCE.getGift(villager);
        if (!stack.isEmpty()) MathUtils.throwItem(villager, stack, new Vec3d(hero.posX, hero.posY, hero.posZ));
        cooldown = 600 + villager.getRNG().nextInt(6001);
        hero = null;
    }
    
    private boolean canSeeHero() {
        if (hero == null) hero = villager.world.getClosestPlayer(villager.posX, villager.posY, villager.posZ, 5,
                p -> ((EntityPlayer) p).isPotionActive(RaidsContent.HERO_OF_THE_VILLAGE));
        return hero != null;
    }
    
}
