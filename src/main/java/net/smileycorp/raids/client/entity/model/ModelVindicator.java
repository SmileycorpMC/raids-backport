package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.common.Constants;

public class ModelVindicator extends ModelIllager {
    
    private static final ResourceLocation UPGRADED_1_HAT = Constants.loc("textures/entity/illager/vindicator_upgraded1_hat.png");
    private static final ResourceLocation UPGRADED_2_HAT = Constants.loc("textures/entity/illager/vindicator_upgraded2_hat.png");
    
    public ModelVindicator() {
        super(0, 0, 64, 64);
        hat = new ModelRenderer(this, 0, 0);
        hat.addBox(-5, -11, -5, 10, 11, 10, 0);
        ModelRenderer horns = new ModelRenderer(this, 0 ,0);
        horns.setRotationPoint(6,-9f, -2f);
        horns.rotateAngleX = 0.5235988f;
        horns.addBox(-1, -4,  -1, 2, 4, 2);
        horns.addBox(-13, -4,  -1, 2, 4, 2);
        hat.addChild(horns);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
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
    
}
