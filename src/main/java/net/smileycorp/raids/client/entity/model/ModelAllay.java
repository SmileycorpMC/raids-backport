package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.common.util.MathUtils;

public class ModelAllay extends ModelBase {
    
    private final ModelRenderer root;
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer right_arm;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_wing;
    private final ModelRenderer left_wing;
    
    public ModelAllay() {
        textureWidth = 32;
        textureHeight = 32;
        root = new ModelRenderer(this);
        root.setRotationPoint(0, 23, 0);
        head = new ModelRenderer(this, 0, 0);
        head.setRotationPoint(0, -3.99f, 0);
        head.addBox(-2.5f, -5, -2.5f, 5, 5, 5);
        root.addChild(head);
        body = new ModelRenderer(this, 0, 10);
        body.setRotationPoint(0, -4, 0);
        body.addBox(-1.5f, 0, -1, 3, 4, 2);
        ModelRenderer body2 = new ModelRenderer(this, 0, 16);
        body2.addBox(-1.5f, 0, -1, 3, 5, 2, -0.2f);
        body.addChild(body2);
        root.addChild(body);
        right_arm = new ModelRenderer(this, 23, 0);
        right_arm.setRotationPoint(-1.75f, 0.5f, 0);
        right_arm.addBox(-0.75f, -0.5f, -1, 1, 4, 2, -0.01f);
        body.addChild(right_arm);
        left_arm = new ModelRenderer(this, 23, 6);
        left_arm.setRotationPoint(1.75f, 0.5f, 0);
        left_arm.addBox(-0.25f, -0.5f, -1, 1, 4, 2, -0.01f);
        body.addChild(left_arm);
        right_wing = new ModelRenderer(this, 16, 14);
        right_wing.setRotationPoint(-0.5f, 0, 0.6f);
        right_wing.addBox(0, 1, 0, 0, 5, 8);
        body.addChild(right_wing);
        left_wing = new ModelRenderer(this, 16, 14);
        left_wing.setRotationPoint(0.5f, 0, 0.6f);
        left_wing.addBox(0, 1, 0, 0, 5, 8);
        body.addChild(left_wing);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float f = ageInTicks * 20f * 0.017453292f + limbSwing;
        float f1 = MathHelper.cos(f) * 3.1415927f * 0.15f + limbSwingAmount;
        float f2 = ageInTicks - (float)entity.ticksExisted;
        float f3 = ageInTicks * 9f * 0.017453292f;
        float f4 = Math.min(ageInTicks / 0.3f, 1.0f);
        float f5 = 1.0f - f4;
        float f6 = ((EntityAllay)entity).getSwingProgress(f2);
        float f12;
        float f13;
        float f14;
        if (((EntityAllay)entity).isDancing()) {
            f12 = ageInTicks * 8f * 0.017453292f + limbSwingAmount;
            f13 = MathHelper.cos(f12) * 16f * 0.017453292f;
            f14 = ((EntityAllay)entity).getSpinningProcess(f2);
            float f10 = MathHelper.cos(f12) * 14f * 0.017453292f;
            float f11 = MathHelper.cos(f12) * 30f * 0.017453292f;
            root.rotateAngleY = ((EntityAllay)entity).isSpinning() ? 12.566371f * f14 : root.rotateAngleY;
            root.rotateAngleZ = f13 * (1 - f14);
            head.rotateAngleY = f11 * (1 - f14);
            head.rotateAngleZ = f10 * (1 - f14);
        } else {
            head.rotateAngleX = headPitch * 0.017453292f;
            head.rotateAngleY = netHeadYaw * 0.017453292f;
        }
        right_wing.rotateAngleX = 0.43633232F * (1.0F - f4);
        right_wing.rotateAngleY = (-(float)Math.PI / 4F) + f1;
        left_wing.rotateAngleX = 0.43633232F * (1.0F - f4);
        left_wing.rotateAngleY = ((float)Math.PI / 4F) - f1;
        body.rotateAngleX = f4 * 0.7853982f;
        f12 = f6 * MathUtils.lerp(f4, -1.0471976f, -1.134464f);
        root.offsetY += (float)Math.cos(f3) * 0.25f * f5;
        right_arm.rotateAngleX = f12;
        left_arm.rotateAngleX = f12;
        f13 = f5 * (1 - f6);
        f14 = 0.43633232f - MathHelper.cos(f3 + 4.712389f) * 3.1415927f * 0.075f * f13;
        left_arm.rotateAngleZ = -f14;
        right_arm.rotateAngleZ = f14;
        right_arm.rotateAngleY = 0.27925268f * f6;
        left_arm.rotateAngleY = -0.27925268f * f6;
    }
    
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        root.render(scale);
    }
    
    public void translateToHand() {
        root.postRender(0.0625f);
        GlStateManager.translate(0.0F, 0.0625f, 0.1875f);
        GlStateManager.scale(0.7F, 0.7f, 0.7f);
        GlStateManager.translate(0.0625f, 0, 0);
    }
    
}
