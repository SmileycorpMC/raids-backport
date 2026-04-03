package net.smileycorp.raids.mixin;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelIllager.class)
public class MixinModelIllager {

    @Shadow public ModelRenderer rightArm;

    @Shadow public ModelRenderer leftArm;

    @Shadow public ModelRenderer leg0;
    
    @Shadow public ModelRenderer leg1;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/AbstractIllager;getArmPose()Lnet/minecraft/entity/monster/AbstractIllager$IllagerArmPose;"), method = "setRotationAngles")
    public void raids$setRotationAngles$getArmPose(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scalefactor, Entity entity, CallbackInfo callback) {
        if (!entity.isRiding()) return;
        rightArm.rotateAngleX = (-(float)Math.PI / 5f);
        rightArm.rotateAngleY = 0;
        rightArm.rotateAngleZ = 0;
        leftArm.rotateAngleX = (-(float)Math.PI / 5f);
        leftArm.rotateAngleY = 0;
        leftArm.rotateAngleZ = 0;
        leg0.rotateAngleX = -1.4137167f;
        leg0.rotateAngleY = ((float)Math.PI / 10f);
        leg0.rotateAngleZ = 0.07853982f;
        leg1.rotateAngleX = -1.4137167f;
        leg1.rotateAngleY = (-(float)Math.PI / 10f);
        leg1.rotateAngleZ = -0.07853982f;
    }


}
