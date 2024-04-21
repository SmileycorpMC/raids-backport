package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelRaidsVex extends ModelBase {
    
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    protected final ModelRenderer leftWing;
    protected final ModelRenderer rightWing;

    public ModelRaidsVex() {
        super();
        textureWidth = 32;
        textureHeight = 32;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 21.5F, 0.0F);
        body.cubeList.add(new ModelBox(body, 0, 10, -1.5F, -4.0F, -1.0F, 3, 4, 2, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 0, 16, -1.5F, -3.0F, -1.0F, 3, 5, 2, -0.2F, false));
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -4.0F, 0.0F);
        body.addChild(head);
        head.cubeList.add(new ModelBox(head, 0, 0, -2.5F, -5.0F, -2.5F, 5, 5, 5, 0.0F, false));
        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-1.75F, -3.75F, 0.0F);
        body.addChild(rightArm);
        rightArm.cubeList.add(new ModelBox(rightArm, 23, 0, -1.25F, -0.5F, -1.0F, 2, 4, 2, -0.1F, false));
        //rightItem = new ModelRenderer(this);
       // rightItem.setRotationPoint(-0.25F, 2.75F, 0.0F);
        //bipedRightArm.addChild(rightItem);
        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(1.75F, -3.75F, 0.0F);
        body.addChild(leftArm);
        leftArm.cubeList.add(new ModelBox(leftArm, 23, 6, -0.75F, -0.5F, -1.0F, 2, 4, 2, -0.1F, false));
        leftWing = new ModelRenderer(this);
        leftWing.mirror = true;
        leftWing.setRotationPoint(0.5F, -3.0F, 1.0F);
        body.addChild(leftWing);
        leftWing.cubeList.add(new ModelBox(leftWing, 16, 22, 0.0F, 0.0F, 0.0F, 8, 5, 0, 0.0F, true));
        rightWing = new ModelRenderer(this);
        rightWing.setRotationPoint(-0.5F, -3.0F, 1.0F);
        body.addChild(rightWing);
        rightWing.cubeList.add(new ModelBox(rightWing, 16, 22, -8.0F, 0.0F, 0.0F, 8, 5, 0, 0.0F, false));
    }
    
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        body.render(scale);
    }
    
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        float f = MathHelper.cos(ageInTicks * 5.5F * ((float)Math.PI / 180F)) * 0.1F;
        rightArm.rotateAngleZ = ((float)Math.PI / 5F) + f;
        leftArm.rotateAngleZ = -(((float)Math.PI / 5F) + f);
        if (entity instanceof EntityVex && ((EntityVex) entity).isCharging()) {
            body.rotateAngleX = 0.0F;
            setArmsCharging(((EntityVex) entity).getHeldItemMainhand(), ((EntityVex) entity).getHeldItemOffhand(), f);
        } else body.rotateAngleX = 0.15707964F;
        leftWing.rotateAngleY = MathHelper.cos(ageInTicks * 45.836624F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 16.2F - 1.0995574F;
        rightWing.rotateAngleY = -leftWing.rotateAngleY;
        leftWing.rotateAngleX = -0.471239F;
        leftWing.rotateAngleZ = -0.94247776F;
        rightWing.rotateAngleX = -0.471239F;
        rightWing.rotateAngleZ = 0.94247776F;
    }
    
    private void setArmsCharging(ItemStack mainhand, ItemStack offhand, float swing) {
        if (mainhand.isEmpty() && offhand.isEmpty()) {
            rightArm.rotateAngleX = -1.2217305F;
            rightArm.rotateAngleY = 0.2617994F;
            rightArm.rotateAngleZ = -0.47123888F - swing;
            leftArm.rotateAngleX = -1.2217305F;
            leftArm.rotateAngleY = -0.2617994F;
            leftArm.rotateAngleZ = 0.47123888F + swing;
        } else {
            if (!mainhand.isEmpty()) {
                rightArm.rotateAngleX = 3.6651914F;
                rightArm.rotateAngleY = 0.2617994F;
                rightArm.rotateAngleZ = -0.47123888F - swing;
            }
            if (!offhand.isEmpty()) {
                leftArm.rotateAngleX = 3.6651914F;
                leftArm.rotateAngleY = -0.2617994F;
                leftArm.rotateAngleZ = 0.47123888F + swing;
            }
        }
    }
    
    public void translateToHand(EnumHandSide side) {
        body.postRender(0.0625F);
        (side == EnumHandSide.LEFT ? leftArm : rightArm).postRender(0.0625F);
        GlStateManager.scale(0.55F, 0.55F, 0.55F);
        GlStateManager.translate(side == EnumHandSide.RIGHT ? 0.046875D : -0.046875D, -0.15625D, 0.078125D);
    }
    
}
