package net.smileycorp.raids.client.entity;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelVindicator;
import net.smileycorp.raids.common.Constants;

public class RenderRaidsVindicator extends RenderLiving<EntityMob> {
    
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/illager/vindicator.png");
    private static final ResourceLocation UPGRADED_1 = Constants.loc("textures/entity/illager/vindicator_upgraded1.png");
    private static final ResourceLocation UPGRADED_2 = Constants.loc("textures/entity/illager/vindicator_upgraded2.png");
    
    public RenderRaidsVindicator(RenderManager rm) {
        super(rm, new ModelVindicator(), 0.5F);
        addLayer(new LayerHeldItem(this) {
            public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (!((EntityVindicator)entity).isAggressive()) return;
                super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
            protected void translateToHand(EnumHandSide hand) {
                ((ModelIllager)livingEntityRenderer.getMainModel()).getArm(hand).postRender(0.0625F);
            }
        });
        addLayer(new LayerCustomHead(((ModelIllager)getMainModel()).head));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityMob entity) {
        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.getItem() instanceof ItemArmor) return ((ItemArmor) chest.getItem()).damageReduceAmount > 6 ? UPGRADED_2 : UPGRADED_1;
        return DEFAULT;
    }
    
    @Override
    protected void preRenderCallback(EntityMob entity, float partialTicks) {
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
    
}
