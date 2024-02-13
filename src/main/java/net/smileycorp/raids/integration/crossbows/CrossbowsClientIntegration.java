package net.smileycorp.raids.integration.crossbows;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.smileycorp.crossbows.client.CrossbowAnimator;

public class CrossbowsClientIntegration {
    public static void animateCharge(EntityLivingBase entity, ModelRenderer rightArm, ModelRenderer leftArm) {
        CrossbowAnimator.animateCharge(entity, rightArm, leftArm);
    }
    
    public static void animateCrossbowHold(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean isRight) {
        CrossbowAnimator.animateCrossbowHold(rightArm, leftArm, head, isRight);
    }
    
}
