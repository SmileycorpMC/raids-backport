package net.smileycorp.raids.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.common.ItemCrossbow;
import net.smileycorp.raids.common.RaidsContent;

import javax.vecmath.Vector3f;

public interface ICrossbowAttackMob extends IRangedAttackMob {

    void setChargingCrossbow(boolean charging);
    
    boolean isChargingCrossbow();
    
    @Override
    default void attackEntityWithRangedAttack(EntityLivingBase entity, float distance) {
        performCrossbowAttack((EntityLivingBase)this, distance);
    }
    
    default void performCrossbowAttack(EntityLivingBase entity, float distance) {
        EnumHand hand = EnumHand.MAIN_HAND;
        ItemStack stack = ItemStack.EMPTY;
        for (EnumHand h : EnumHand.values()) if (entity.getHeldItem(h).getItem() == RaidsContent.CROSSBOW) {
            stack = entity.getHeldItem(h);
            hand = h;
            break;
        }
        if (stack.isEmpty()) return;
        ItemCrossbow.performShooting(entity.world, entity, hand, stack, distance, (float)(14 - entity.getEntityWorld().getDifficulty().getDifficultyId() * 4));
        onCrossbowAttackPerformed();
    }

    default void shootCrossbowProjectile(EntityLivingBase entity, EntityLivingBase target, Entity p_32325_, float p_32326_, float p_32327_) {
        double d0 = target.posX - entity.posX;
        double d1 = target.posZ - entity.posZ;
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = target.posY - p_32325_.posY + d2 * (double)0.2F;
        Vector3f vector3f = this.getProjectileShotVector(entity, new Vec3d(d0, d3, d1), p_32326_);
        ((IProjectile)p_32325_).shoot(vector3f.x, vector3f.y, vector3f.z, p_32327_, (float)(14 - entity.world.getDifficulty().getDifficultyId() * 4));
        entity.playSound(RaidsContent.CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    default Vector3f getProjectileShotVector(EntityLivingBase p_32333_, Vec3d p_32334_, float p_32335_) {
        Vec3d vec3 = p_32334_.normalize();
        Vec3d vec31 = vec3.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (vec31.lengthSquared() <= 1.0E-7D) {
            //vec31 = vec3.crossProduct(p_32333_.getL);
        }

        Vector3f angle = new Vector3f((float) vec31.x, (float) vec31.y, (float) vec31.z);
        Vector3f vector3f = new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
        vector3f.angle(angle);
        return vector3f;
    }

    void onCrossbowAttackPerformed();

    EntityLivingBase getTarget();

}
