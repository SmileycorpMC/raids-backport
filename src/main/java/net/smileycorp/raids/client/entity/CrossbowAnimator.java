package net.smileycorp.raids.client.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.item.ItemCrossbow;

public class CrossbowAnimator {
    
    public static void animateCharge(EntityLivingBase entity, ModelRenderer rightArm, ModelRenderer leftArm) {
        boolean isRight = entity.getActiveHand() == EnumHand.MAIN_HAND ^ entity.getPrimaryHand() == EnumHandSide.LEFT;
        ModelRenderer hand = isRight ? rightArm : leftArm;
        ModelRenderer hand1 = isRight ? leftArm : rightArm;
        hand.rotateAngleY = isRight ? -0.8F : 0.8F;
        hand.rotateAngleX = -0.97079635F;
        hand1.rotateAngleX = hand.rotateAngleX;
        float f = (float) ItemCrossbow.getChargeDuration(entity.getActiveItemStack());
        float f1 = -MathHelper.clamp((float)entity.getItemInUseCount(), 0.0F, f);
        float f2 = f1 / f;
        hand1.rotateAngleY = MathUtils.lerp(f2, 0.4F, 0.85F) * (float)(isRight ? 1 : -1);
        hand1.rotateAngleX = MathUtils.lerp(f2, hand1.rotateAngleX, (-(float)Math.PI / 2F));
    }
    
    public static void animateCrossbowHold(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean isRight) {
        ModelRenderer hand1 = isRight ? rightArm : leftArm;
        ModelRenderer hand2 = isRight ? leftArm : rightArm;
        hand1.rotateAngleY = (isRight ? -0.3F : 0.3F) + head.rotateAngleY;
        hand2.rotateAngleY = (isRight ? 0.6F : -0.6F) + head.rotateAngleY;
        hand1.rotateAngleX= (-(float)Math.PI / 2F) + head.rotateAngleX + 0.1F;
        hand2.rotateAngleX= -1.5F + head.rotateAngleX;
    }
    
}
