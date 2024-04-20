package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelRaidsVex extends ModelBiped {
    
    protected ModelRenderer leftWing;
    protected ModelRenderer rightWing;

    public ModelRaidsVex() {
        super(0, 0, 32, 32);
        bipedHead = new ModelRenderer(this, 0, 0).addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5);
        bipedHead.setRotationPoint(0.0F, 20.0F, 0.0F);
        bipedBody = new ModelRenderer(this, 0, 10).addBox(-1.5F, 0.0F, -1.0F, 3, 4, 2);
        bipedBody.setRotationPoint(0.0F, 20.0F, 0.0F);
        ModelRenderer bodyBottom = new ModelRenderer(this, 0, 16).addBox(-1.5F, 1.0F, -1.0F, 3, 5, 2);
        bipedBody.addChild(bodyBottom);
        bipedRightArm = new ModelRenderer(this, 23, 0).addBox(-1.25F, -0.5F, -1.0F, 2, 4, 2);
        bipedRightArm.setRotationPoint(0.5F, 1.0F, 1.0F);
        bipedLeftArm  = new ModelRenderer(this, 23, 6).addBox(-0.75F, -0.5F, -1.0F, 2, 4, 2);
        bipedLeftArm.setRotationPoint(-0.5F, 1.0F, 1.0F);
        leftWing = new ModelRenderer(this,16, 14).addBox(0.0F, 0.0F, 0.0F, 0, 5, 8);
        leftWing.setRotationPoint(0.5F, 1.0F, 1.0F);
        rightWing = new ModelRenderer(this, 16, 14).addBox(0.0F, 0.0F, 0.0F, 0, 5, 8);
        rightWing.setRotationPoint(-0.5F, 1.0F, 1.0F);
        bipedLeftLeg.showModel = false;
        bipedRightLeg.showModel = false;
        bipedHeadwear.showModel = false;
    }
    
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        rightWing.render(scale);
        leftWing.render(scale);
    }
    
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        float f = MathHelper.cos(ageInTicks * 5.5F * ((float)Math.PI / 180F)) * 0.1F;
        bipedRightArm.rotateAngleZ = ((float)Math.PI / 5F) + f;
        bipedLeftArm.rotateAngleZ = -(((float)Math.PI / 5F) + f);
        if (entity instanceof EntityVex && ((EntityVex) entity).isCharging()) {
            bipedBody.rotateAngleX = 0.0F;
            setArmsCharging(((EntityVex) entity).getHeldItemMainhand(), ((EntityVex) entity).getHeldItemOffhand(), f);
        } else bipedBody.rotateAngleX = 0.15707964F;
        leftWing.rotateAngleY= 1.0995574F + MathHelper.cos(ageInTicks * 45.836624F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 16.2F;
        rightWing.rotateAngleY = -leftWing.rotateAngleY;
        leftWing.rotateAngleX = 0.47123888F;
        leftWing.rotateAngleZ = -0.47123888F;
        rightWing.rotateAngleX = 0.47123888F;
        rightWing.rotateAngleZ = 0.47123888F;
    }
    
    private void setArmsCharging(ItemStack mainhand, ItemStack offhand, float swing) {
        if (mainhand.isEmpty() && offhand.isEmpty()) {
            bipedRightArm.rotateAngleX = -1.2217305F;
            bipedRightArm.rotateAngleY = 0.2617994F;
            bipedRightArm.rotateAngleZ = -0.47123888F - swing;
            bipedLeftArm.rotateAngleX = -1.2217305F;
            bipedLeftArm.rotateAngleY = -0.2617994F;
            bipedLeftArm.rotateAngleZ = 0.47123888F + swing;
        } else {
            if (!mainhand.isEmpty()) {
                bipedRightArm.rotateAngleX = 3.6651914F;
                bipedRightArm.rotateAngleY = 0.2617994F;
                bipedRightArm.rotateAngleZ = -0.47123888F - swing;
            }
            if (!offhand.isEmpty()) {
                bipedLeftArm.rotateAngleX = 3.6651914F;
                bipedLeftArm.rotateAngleY = -0.2617994F;
                bipedLeftArm.rotateAngleZ = 0.47123888F + swing;
            }
        }
    }
    
    @Override
    public void postRenderArm(float scale, EnumHandSide side) {
        super.postRenderArm(scale, side);
        GlStateManager.scale(0.55F, 0.55F, 0.55F);
        GlStateManager.translate(side == EnumHandSide.RIGHT ? 0.046875D : -0.046875D, -0.15625D, 0.078125D);
    }

}
