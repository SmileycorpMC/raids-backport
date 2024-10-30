package net.smileycorp.raids.client.entity;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelPillager;
import net.smileycorp.raids.common.Constants;

public class RenderPillager extends RenderLiving<EntityMob> {
	
	private static final ResourceLocation DEFAULT = Constants.loc("textures/entity/illager/pillager.png");
    private static final ResourceLocation UPGRADED_1 = Constants.loc("textures/entity/illager/pillager_upgraded1.png");
    private static final ResourceLocation UPGRADED_2 = Constants.loc("textures/entity/illager/pillager_upgraded2.png");

    public RenderPillager(RenderManager rm) {
        super(rm, new ModelPillager(), 0.5f);
        addLayer(new LayerHeldItem(this) {
			protected void translateToHand(EnumHandSide hand) {
                ((ModelIllager)livingEntityRenderer.getMainModel()).getArm(hand).postRender(0.0625f);
            }
        });
        addLayer(new LayerCustomHead(((ModelPillager)getMainModel()).head));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityMob entity) {
        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.getItem() instanceof ItemArmor) return ((ItemArmor) chest.getItem()).damageReduceAmount > 6 ? UPGRADED_2 : UPGRADED_1;
        return DEFAULT;
    }

    @Override
	protected void preRenderCallback(EntityMob entity, float partialTicks) {
        GlStateManager.scale(0.9375f, 0.9375f, 0.9375f);
    }
    
}
