package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class EntityAIRavagerAttackMelee extends EntityAIAttackMelee {
    
    public EntityAIRavagerAttackMelee(EntityCreature entity) {
        super(entity, 1.0D, true);
    }
    
    @Override
    protected double getAttackReachSqr(EntityLivingBase target) {
        float distance = attacker.width - 0.1F;
        return distance * 2.0F * distance * 2.0F + target.width;
    }
    
}
