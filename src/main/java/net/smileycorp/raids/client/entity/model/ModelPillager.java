package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.atlas.api.util.MathUtils;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;

public class ModelPillager extends ModelIllager {
    
    private static final ResourceLocation UPGRADED_1_HAT = Constants.loc("textures/entity/illager/pillager_upgraded1_hat.png");
    private static final ResourceLocation UPGRADED_2_HAT = Constants.loc("textures/entity/illager/pillager_upgraded2_hat.png");
    
    public ModelPillager() {
        super(0.0F, 0.0F, 64, 64);
        hat = new ModelRenderer(this, 0, 0);
        hat.addBox(-5, -11, -5, 10, 8, 10, 0);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        if (entity instanceof EntityPillager && ModIntegration.HAS_CROSSBOW_MOD) {
            if (((EntityPillager) entity).isChargingCrossbow()) animateCharge((EntityLivingBase) entity, rightArm, leftArm);
            else {
                if (ModIntegration.isCrossbow(((EntityPillager) entity).getHeldItemOffhand()))
                    animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.LEFT);
                if (ModIntegration.CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(((EntityLivingBase) entity).getHeldItemMainhand()))
                    animateCrossbowHold(rightArm, leftArm, head, ((EntityLivingBase) entity).getPrimaryHand() == EnumHandSide.RIGHT);
            }
        }
        copyModelAngles(head, hat);
    }
    
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (! (entity instanceof EntityLivingBase)) return;
        ItemStack chest = ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof ItemArmor)) return;
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(
                ((ItemArmor) chest.getItem()).damageReduceAmount > 6 ? UPGRADED_2_HAT : UPGRADED_1_HAT);
        hat.render(scale);
    }
    
    public static void animateCharge(EntityLivingBase entity, ModelRenderer rightArm, ModelRenderer leftArm) {
        rightArm.rotateAngleX = 0;
        rightArm.rotateAngleY = 0;
        rightArm.rotateAngleZ = 0;
        leftArm.rotateAngleX = 0;
        leftArm.rotateAngleY = 0;
        leftArm.rotateAngleZ = 0;
        boolean isRight = entity.getActiveHand() == EnumHand.MAIN_HAND ^ entity.getPrimaryHand() == EnumHandSide.LEFT;
        ModelRenderer hand = isRight ? rightArm : leftArm;
        ModelRenderer hand1 = isRight ? leftArm : rightArm;
        hand.rotateAngleY = isRight ? -0.8F : 0.8F;
        hand.rotateAngleX = -0.97079635F;
        hand1.rotateAngleX = hand.rotateAngleX;
        float f2 = MathHelper.clamp(ModIntegration.getChargeAmount(entity.getActiveItemStack(), entity),-1, 0);
        hand1.rotateAngleY = MathUtils.lerp(f2, 0.4F, 0.85F) * (float)(isRight ? 1 : -1);
        hand1.rotateAngleX = MathUtils.lerp(f2, hand1.rotateAngleX, (-(float)Math.PI / 2F));
    }
    
    public static void animateCrossbowHold(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean isRight) {
        rightArm.rotateAngleX = 0;
        rightArm.rotateAngleY = 0;
        rightArm.rotateAngleZ = 0;
        leftArm.rotateAngleX = 0;
        leftArm.rotateAngleY = 0;
        leftArm.rotateAngleZ = 0;
        ModelRenderer hand1 = isRight ? rightArm : leftArm;
        ModelRenderer hand2 = isRight ? leftArm : rightArm;
        hand1.rotateAngleY = (isRight ? -0.3F : 0.3F) + head.rotateAngleY;
        hand2.rotateAngleY = (isRight ? 0.6F : -0.6F) + head.rotateAngleY;
        hand1.rotateAngleX = (-(float)Math.PI / 2F) + head.rotateAngleX + 0.1F;
        hand2.rotateAngleX = -1.5F + head.rotateAngleX;
    }
    
}
