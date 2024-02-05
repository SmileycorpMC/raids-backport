package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.smileycorp.raids.client.entity.CrossbowAnimator;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.ICrossbowAttackMob;

public class ModelPillager extends ModelIllager {
    
    public ModelPillager() {
        super(0.0F, 0.0F, 64, 64);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        if (entity instanceof ICrossbowAttackMob && entity instanceof EntityLivingBase) {
            if (((ICrossbowAttackMob) entity).isChargingCrossbow()) {
                CrossbowAnimator.animateCharge((EntityLivingBase) entity, rightArm, leftArm);
                return;
            }
            if (((EntityLivingBase) entity).getHeldItemOffhand().getItem() == RaidsContent.CROSSBOW) {
                CrossbowAnimator.animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.LEFT);
            }
            if (((EntityLivingBase) entity).getHeldItemMainhand().getItem() == RaidsContent.CROSSBOW) {
                CrossbowAnimator.animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.RIGHT);
            }
        }
    }
    
}
