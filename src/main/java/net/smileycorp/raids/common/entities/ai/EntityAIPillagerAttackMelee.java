package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.smileycorp.raids.integration.ModIntegration;

public class EntityAIPillagerAttackMelee extends EntityAIAttackMelee {

    public EntityAIPillagerAttackMelee(EntityCreature creature) {
        super(creature, 2, false);
    }

    @Override
    public boolean shouldExecute() {
        ItemStack stack = attacker.getHeldItemMainhand();
        return (attacker.isInWater() || (!stack.isEmpty() &! (stack.getItem() instanceof ItemBow)
                &! ModIntegration.isCrossbow(stack))) && super.shouldExecute();
    }

}
