package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsClientIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsIntegration;

public class ModelPillager extends ModelIllager {
    
    public ModelPillager() {
        super(0.0F, 0.0F, 64, 64);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        if (entity instanceof EntityPillager) {
            if (ModIntegration.CROSSBOWS_LOADED && ((EntityPillager) entity).isChargingCrossbow()) {
                CrossbowsClientIntegration.animateCharge((EntityLivingBase) entity, rightArm, leftArm);
                return;
            }
            if (ModIntegration.CROSSBOWS_LOADED && CrossbowsIntegration.isCrossbow(((EntityLivingBase) entity).getHeldItemOffhand())) {
                CrossbowsClientIntegration.animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.LEFT);
            }
            if (ModIntegration.CROSSBOWS_LOADED && CrossbowsIntegration.isCrossbow(((EntityLivingBase) entity).getHeldItemMainhand()))  {
                CrossbowsClientIntegration.animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.RIGHT);
            }
        }
    }
    
}
