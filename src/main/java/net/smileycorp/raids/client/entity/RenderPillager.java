package net.smileycorp.raids.client.entity;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelPillager;
import net.smileycorp.raids.common.Constants;

public class RenderPillager extends RenderLiving<EntityMob> {
	
	private static final ResourceLocation texture = Constants.loc("textures/entity/illager/pillager.png");

    public RenderPillager(RenderManager rm) {
        super(rm, new ModelPillager(), 0.5F);
        addLayer(new LayerHeldItem(this) {
			protected void translateToHand(EnumHandSide hand) {
                ((ModelIllager)livingEntityRenderer.getMainModel()).getArm(hand).postRender(0.0625F);
            }
        });
    }
    
    @Override
	protected ResourceLocation getEntityTexture(EntityMob entity) {
        return texture;
    }

    @Override
	protected void preRenderCallback(EntityMob entity, float partialTicks) {
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
