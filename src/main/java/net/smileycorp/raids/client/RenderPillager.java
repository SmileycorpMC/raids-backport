package net.smileycorp.raids.client;

import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.common.ModDefinitions;

public class RenderPillager extends RenderLiving<EntityMob> {
	
	private static final ResourceLocation texture = ModDefinitions.getResource("textures/entity/illager/pillager.png");

    public RenderPillager(RenderManager p_i47207_1_) {
        super(p_i47207_1_, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
        this.addLayer(new LayerHeldItem(this) {
            @Override
			protected void translateToHand(EnumHandSide hand)
            {
                ((ModelIllager)this.livingEntityRenderer.getMainModel()).getArm(hand).postRender(0.0625F);
            }
        });
    }
    
    @Override
	protected ResourceLocation getEntityTexture(EntityMob entity) {
        return texture;
    }

    @Override
	protected void preRenderCallback(EntityMob entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
