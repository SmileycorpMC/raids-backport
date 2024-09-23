package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.smileycorp.raids.common.RaidsContent;
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
        if (!stack.isEmpty()) {
            double y = villager.posY + villager.getEyeHeight() - 0.30000001192092896;
            EntityItem item = new EntityItem(villager.world, villager.posX, y, villager.posZ, stack);
            double dx = hero.posX - villager.posX;
            double dy = hero.posY - y;
            double dz = hero.posZ - villager.posZ;
            double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
            item.motionX = dx / magnitude;
            item.motionY = dy / magnitude;
            item.motionZ = dz / magnitude;
            villager.world.spawnEntity(item);
        }
        cooldown = 600 + villager.getRNG().nextInt(6001);
        hero = null;
    }
    
    private boolean canSeeHero() {
        if (hero == null) hero = villager.world.getClosestPlayer(villager.posX, villager.posY, villager.posZ, 5,
                p -> ((EntityPlayer) p).isPotionActive(RaidsContent.HERO_OF_THE_VILLAGE));
        return hero != null;
    }
    
}
