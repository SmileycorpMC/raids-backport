package net.smileycorp.raids.mixin;

import io.netty.util.internal.MathUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.raids.common.ItemCrossbow;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow public ModelRenderer bipedRightArm;
    @Shadow public ModelRenderer bipedLeftArm;

    @Shadow public ModelBiped.ArmPose leftArmPose;

    @Shadow public ModelBiped.ArmPose rightArmPose;

    @Shadow public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    public void setRotationAngles$HEAD(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.getActiveItemStack().getItem() == RaidsContent.CROSSBOW) {
                leftArmPose = ModelBiped.ArmPose.EMPTY;
                rightArmPose = ModelBiped.ArmPose.EMPTY;
                return;
            }
            if (player.getHeldItemMainhand().getItem() == RaidsContent.CROSSBOW && ItemCrossbow.isCharged(player.getHeldItemMainhand())) {
                if (player.getPrimaryHand() == EnumHandSide.RIGHT) rightArmPose = ModelBiped.ArmPose.EMPTY;
                else leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
            if (player.getHeldItemOffhand().getItem() == RaidsContent.CROSSBOW && ItemCrossbow.isCharged(player.getHeldItemOffhand())) {
                if (player.getPrimaryHand() == EnumHandSide.LEFT) rightArmPose = ModelBiped.ArmPose.EMPTY;
                else leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
        }
    }

    @Inject(method = "setRotationAngles", at = @At("TAIL"), cancellable = true)
    public void setRotationAngles$TAIL(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.getActiveItemStack().getItem() == RaidsContent.CROSSBOW) {
                boolean isRight = player.getActiveHand() == EnumHand.MAIN_HAND ^ player.getPrimaryHand() == EnumHandSide.LEFT;
                ModelRenderer hand = isRight ? bipedRightArm : bipedLeftArm;
                ModelRenderer hand1 = isRight ? bipedLeftArm : bipedRightArm;
                hand.rotateAngleY = isRight ? -0.8F : 0.8F;
                hand.rotateAngleX = -0.97079635F;
                hand1.rotateAngleX = hand.rotateAngleX;
                float f = (float) ItemCrossbow.getChargeDuration(player.getActiveItemStack());
                float f1 = -MathHelper.clamp((float)player.getItemInUseCount(), 0.0F, f);
                float f2 = f1 / f;
                hand1.rotateAngleY = lerp(f2, 0.4F, 0.85F) * (float)(isRight ? 1 : -1);
                hand1.rotateAngleX = lerp(f2, hand1.rotateAngleX, (-(float)Math.PI / 2F));
                return;
            }
            ItemStack mainhand = player.getHeldItemMainhand();
            ItemStack offhand = player.getHeldItemOffhand();
            if (offhand.getItem() == RaidsContent.CROSSBOW && ItemCrossbow.isCharged(offhand)) {
                animateCrossbowHold(player.getPrimaryHand() == EnumHandSide.LEFT);
            }
            if (mainhand.getItem() == RaidsContent.CROSSBOW && ItemCrossbow.isCharged(mainhand)) {
                animateCrossbowHold(player.getPrimaryHand() == EnumHandSide.RIGHT);
            }
        }
    }

    private void animateCrossbowHold(boolean isRight) {
        ModelRenderer hand1 = isRight ? bipedRightArm : bipedLeftArm;
        ModelRenderer hand2 = isRight ? bipedLeftArm : bipedRightArm;
        hand1.rotateAngleY = (isRight ? -0.3F : 0.3F) + bipedHead.rotateAngleY;
        hand2.rotateAngleY = (isRight ? 0.6F : -0.6F) + bipedHead.rotateAngleY;
        hand1.rotateAngleX= (-(float)Math.PI / 2F) + bipedHead.rotateAngleX + 0.1F;
        hand2.rotateAngleX= -1.5F + bipedHead.rotateAngleX;
    }

    private float lerp(float p_14180_, float p_14181_, float p_14182_) {
        return p_14181_ + p_14180_ * (p_14182_ - p_14181_);
    }

}
