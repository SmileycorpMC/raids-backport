package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.atlas.api.util.MathUtils;
import net.smileycorp.raids.common.entities.EntityRavager;

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
        neck = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        neck.setRotationPoint(0, -7, -1.5f);
        neck.setTextureOffset(68, 73).addBox(-5, -1, -18, 10, 10, 18, 0);
        head = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        head.setRotationPoint(0, 16, -17);
        head.setTextureOffset(0, 0).addBox(-8, -20, -14, 16, 20, 16, 0);
        head.setTextureOffset(0, 0).addBox(-2, -6, -18, 4, 8, 4, 0);
        ModelRenderer modelrenderer = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        modelrenderer.setRotationPoint(-10, -14, -8);
        modelrenderer.setTextureOffset(74, 55).addBox(0, -14, -2, 2, 14, 4, 0);
        modelrenderer.rotateAngleX = 1.0995574f;
        head.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        modelrenderer1.mirror = true;
        modelrenderer1.setRotationPoint(8, -14, -8);
        modelrenderer1.setTextureOffset(74, 55).addBox(0, -14, -2, 2, 14, 4, 0);
        modelrenderer1.rotateAngleX = 1.0995574f;
        head.addChild(modelrenderer1);
        mouth = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        mouth.setRotationPoint(0, -2, 2);
        mouth.setTextureOffset(0, 36).addBox(-8, 0, -16, 16, 3, 16, 0);
        head.addChild(mouth);
        neck.addChild(head);
        body = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        body.setTextureOffset(0, 55).addBox(-7, -10, -7, 14, 16, 20, 0);
        body.setTextureOffset(0, 91).addBox(-6, 6, -7, 12, 13, 18, 0);
        body.setRotationPoint(0, 1, 2);
        leg0 = new ModelRenderer(this, 96, 0);
        leg0.addBox(-4, 0, -4, 8, 37, 8, 0);
        leg0.setRotationPoint(-8, -13, 18);
        leg1 = new ModelRenderer(this, 96, 0);
        leg1.mirror = true;
        leg1.addBox(-4, 0, -4, 8, 37, 8, 0);
        leg1.setRotationPoint(8, -13, 18);
        leg2 = new ModelRenderer(this, 64, 0);
        leg2.addBox(-4, 0, -4, 8, 37, 8, 0);
        leg2.setRotationPoint(-8, -13, -5);
        leg3 = new ModelRenderer(this, 64, 0);
        leg3.mirror = true;
        leg3.addBox(-4, 0, -4, 8, 37, 8, 0);
        leg3.setRotationPoint(8, -13, -5);
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
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scalefactor, Entity entity) {
        head.rotateAngleX = headPitch * ((float)Math.PI / 180f);
        head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180f);
        body.rotateAngleX = ((float)Math.PI / 2f);
        float f = 0.4f * limbSwingAmount;
        leg0.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * f;
        leg1.rotateAngleX = MathHelper.cos(limbSwing* 0.6662f + (float)Math.PI) * f;
        leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * f;
        leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * f;
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
            float f = MathUtils.wrap((float) l - partialTicks, 10);
            float f1 = (1 + f) * 0.5f;
            float f2 = f1 * f1 * f1 * 12;
            float f3 = f2 * MathHelper.sin(neck.rotateAngleX);
            neck.rotationPointZ = -6.5f + f2;
            neck.rotationPointY = -7 - f3;
            if (l > 5) mouth.rotateAngleX = MathHelper.sin(((float) (-4 + l) - partialTicks) / 4) * (float) Math.PI * 0.4f;
            else mouth.rotateAngleX = 0.15707964f * MathHelper.sin((float) Math.PI * ((float) l - partialTicks) / 10);
        } else {
            float f6 = -1 * MathHelper.sin(neck.rotateAngleX);
            neck.rotationPointX = 0;
            neck.rotationPointY = -7 - f6;
            neck.rotationPointZ = 5.5f;
            boolean flag = i > 0;
            neck.rotateAngleX = flag ? 0.21991149f : 0;
            mouth.rotateAngleX = (float) Math.PI * (flag ? 0.05f : 0.01f);
            if (flag) {
                double d0 = (double) i / 40.0D;
                neck.rotationPointX = (float) Math.sin(d0 * 10.0D) * 3;
            } else if (j > 0) {
                float f7 = MathHelper.sin(((float) (20 - j) - partialTicks) / 20 * (float) Math.PI * 0.25f);
                mouth.rotateAngleX = ((float) Math.PI / 2f) * f7;
            }
        }
    }
    
}
