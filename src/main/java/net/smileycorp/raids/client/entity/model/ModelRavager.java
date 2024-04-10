package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.util.MathUtils;

public class ModelRavager extends ModelBase {
    
    private final ModelRenderer head;
    private final ModelRenderer mouth;
    private final ModelRenderer body;
    private final ModelRenderer leg0;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer neck;
    
    public ModelRavager() {
        textureWidth = 128;
        textureHeight = 128;
        int i = 16;
        float f = 0.0F;
        neck = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        neck.setRotationPoint(0.0F, -7.0F, -1.5F);
        neck.setTextureOffset(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10, 10, 18, 0.0F);
        head = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        head.setRotationPoint(0.0F, 16.0F, -17.0F);
        head.setTextureOffset(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16, 20, 16, 0.0F);
        head.setTextureOffset(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4, 8, 4, 0.0F);
        ModelRenderer modelrenderer = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        modelrenderer.setRotationPoint(-10.0F, -14.0F, -8.0F);
        modelrenderer.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
        modelrenderer.rotateAngleX = 1.0995574F;
        head.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        modelrenderer1.mirror = true;
        modelrenderer1.setRotationPoint(8.0F, -14.0F, -8.0F);
        modelrenderer1.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
        modelrenderer1.rotateAngleX = 1.0995574F;
        head.addChild(modelrenderer1);
        mouth = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        mouth.setRotationPoint(0.0F, -2.0F, 2.0F);
        mouth.setTextureOffset(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16, 3, 16, 0.0F);
        head.addChild(mouth);
        neck.addChild(head);
        body = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        body.setTextureOffset(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14, 16, 20, 0.0F);
        body.setTextureOffset(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12, 13, 18, 0.0F);
        body.setRotationPoint(0.0F, 1.0F, 2.0F);
        leg0 = new ModelRenderer(this, 96, 0);
        leg0.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
        leg0.setRotationPoint(-8.0F, -13.0F, 18.0F);
        leg1 = new ModelRenderer(this, 96, 0);
        leg1.mirror = true;
        leg1.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
        leg1.setRotationPoint(8.0F, -13.0F, 18.0F);
        leg2 = new ModelRenderer(this, 64, 0);
        leg2.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
        leg2.setRotationPoint(-8.0F, -13.0F, -5.0F);
        leg3 = new ModelRenderer(this, 64, 0);
        leg3.mirror = true;
        leg3.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
        leg3.setRotationPoint(8.0F, -13.0F, -5.0F);
    }
    
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        body.render(scale);
        leg0.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        neck.render(scale);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        body.rotateAngleX = ((float)Math.PI / 2F);
        float f = 0.4F * limbSwingAmount;
        leg0.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
        leg1.rotateAngleX = MathHelper.cos(limbSwing* 0.6662F + (float)Math.PI) * f;
        leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
        leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
    }
    
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        if (!(entity instanceof EntityRavager)) return;
        EntityRavager ravager = (EntityRavager) entity;
        int i = ravager.getStunnedTick();
        int j = ravager.getRoarTick();
        int l = ravager.getAttackTick();
        if (l > 0) {
            float f = MathUtils.wrap((float) l - partialTicks, 10.0F);
            float f1 = (1.0F + f) * 0.5F;
            float f2 = f1 * f1 * f1 * 12.0F;
            float f3 = f2 * MathHelper.sin(neck.rotateAngleX);
            neck.rotationPointZ = -6.5F + f2;
            neck.rotationPointY = -7.0F - f3;
            if (l > 5) mouth.rotateAngleX = MathHelper.sin(((float) (-4 + l) - partialTicks) / 4.0F) * (float) Math.PI * 0.4F;
            else mouth.rotateAngleX = 0.15707964F * MathHelper.sin((float) Math.PI * ((float) l - partialTicks) / 10.0F);
        } else {
            float f6 = -1.0F * MathHelper.sin(neck.rotateAngleX);
            neck.rotationPointX = 0.0F;
            neck.rotationPointY = -7.0F - f6;
            neck.rotationPointZ = 5.5F;
            boolean flag = i > 0;
            neck.rotateAngleX = flag ? 0.21991149F : 0.0F;
            mouth.rotateAngleX = (float) Math.PI * (flag ? 0.05F : 0.01F);
            if (flag) {
                double d0 = (double) i / 40.0D;
                neck.rotationPointX = (float) Math.sin(d0 * 10.0D) * 3.0F;
            } else if (j > 0) {
                float f7 = MathHelper.sin(((float) (20 - j) - partialTicks) / 20.0F * (float) Math.PI * 0.25F);
                mouth.rotateAngleX = ((float) Math.PI / 2F) * f7;
            }
        }
    }
    
}
