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
        body.setRotationPoint(0, 21.5f, 0);
        body.cubeList.add(new ModelBox(body, 0, 10, -1.5f, -4, -1, 3, 4, 2, 0, false));
        body.cubeList.add(new ModelBox(body, 0, 16, -1.5f, -3, -1, 3, 5, 2, -0.2f, false));
        head = new ModelRenderer(this);
        head.setRotationPoint(0, -4, 0);
        body.addChild(head);
        head.cubeList.add(new ModelBox(head, 0, 0, -2.5f, -5, -2.5f, 5, 5, 5, 0, false));
        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-1.75f, -3.75f, 0);
        body.addChild(rightArm);
        rightArm.cubeList.add(new ModelBox(rightArm, 23, 0, -1.25f, -0.5f, -1, 2, 4, 2, -0.1f, false));
        //rightItem = new ModelRenderer(this);
       // rightItem.setRotationPoint(-0.25f, 2.75f, 0);
        //bipedRightArm.addChild(rightItem);
        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(1.75f, -3.75f, 0);
        body.addChild(leftArm);
        leftArm.cubeList.add(new ModelBox(leftArm, 23, 6, -0.75f, -0.5f, -1, 2, 4, 2, -0.1f, false));
        leftWing = new ModelRenderer(this);
        leftWing.mirror = true;
        leftWing.setRotationPoint(0.5f, -3, 1);
        body.addChild(leftWing);
        leftWing.cubeList.add(new ModelBox(leftWing, 16, 22, 0, 0, 0, 8, 5, 0, 0, true));
        rightWing = new ModelRenderer(this);
        rightWing.setRotationPoint(-0.5f, -3, 1);
        body.addChild(rightWing);
        rightWing.cubeList.add(new ModelBox(rightWing, 16, 22, -8, 0, 0, 8, 5, 0, 0, false));
    }
    
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        body.render(scale);
    }
    
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scalefactor, Entity entity) {
        head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180f);
        head.rotateAngleX = headPitch * ((float)Math.PI / 180f);
        float f = MathHelper.cos(ageInTicks * 5.5f * ((float)Math.PI / 180f)) * 0.1f;
        rightArm.rotateAngleZ = ((float)Math.PI / 5f) + f;
        leftArm.rotateAngleZ = -(((float)Math.PI / 5f) + f);
        if (entity instanceof EntityVex && ((EntityVex) entity).isCharging()) {
            body.rotateAngleX = 0;
            setArmsCharging(((EntityVex) entity).getHeldItemMainhand(), ((EntityVex) entity).getHeldItemOffhand(), f);
        } else body.rotateAngleX = 0.15707964f;
        leftWing.rotateAngleY = MathHelper.cos(ageInTicks * 45.836624f * ((float)Math.PI / 180f)) * ((float)Math.PI / 180f) * 16.2f - 1.0995574f;
        rightWing.rotateAngleY = -leftWing.rotateAngleY;
        leftWing.rotateAngleX = -0.471239f;
        leftWing.rotateAngleZ = -0.94247776f;
        rightWing.rotateAngleX = -0.471239f;
        rightWing.rotateAngleZ = 0.94247776f;
    }
    
    private void setArmsCharging(ItemStack mainhand, ItemStack offhand, float swing) {
        if (mainhand.isEmpty() && offhand.isEmpty()) {
            rightArm.rotateAngleX = -1.2217305f;
            rightArm.rotateAngleY = 0.2617994f;
            rightArm.rotateAngleZ = -0.47123888f - swing;
            leftArm.rotateAngleX = -1.2217305f;
            leftArm.rotateAngleY = -0.2617994f;
            leftArm.rotateAngleZ = 0.47123888f + swing;
        } else {
            if (!mainhand.isEmpty()) {
                rightArm.rotateAngleX = 3.6651914f;
                rightArm.rotateAngleY = 0.2617994f;
                rightArm.rotateAngleZ = -0.47123888f - swing;
            }
            if (!offhand.isEmpty()) {
                leftArm.rotateAngleX = 3.6651914f;
                leftArm.rotateAngleY = -0.2617994f;
                leftArm.rotateAngleZ = 0.47123888f + swing;
            }
        }
    }
    
    public void translateToHand(EnumHandSide side) {
        body.postRender(0.0625f);
        (side == EnumHandSide.LEFT ? leftArm : rightArm).postRender(0.0625f);
        GlStateManager.scale(0.55f, 0.55f, 0.55f);
        GlStateManager.translate(side == EnumHandSide.RIGHT ? 0.046875d : -0.046875d, -0.15625d, 0.078125d);
    }
    
}
