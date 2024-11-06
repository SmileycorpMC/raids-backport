package net.smileycorp.raids.client.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelAllay;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.entities.EntityAllay;

public class RenderAllay extends RenderLiving<EntityAllay> {
    
    private static final ResourceLocation TEXTURE = Constants.loc("textures/entity/allay.png");
    
    public RenderAllay(RenderManager rm) {
        super(rm, new ModelAllay(), 0.4F);
        addLayer(new LayerHeldItemAllay(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityAllay allay) {
        return TEXTURE;
    }
    
    @Override
    public void doRender(EntityAllay allay, double x, double y, double z, float yaw, float pt) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        super.doRender(allay, x, y, z, yaw, pt);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }
    
    @Override
    public void setLightmap(EntityAllay allay) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15, 15);
    }
    
    private static class LayerHeldItemAllay extends LayerHeldItem {
        public LayerHeldItemAllay(RenderAllay renderer) {
            super(renderer);
        }
        
        @Override
        protected void translateToHand(EnumHandSide side) {
            ((ModelAllay)livingEntityRenderer.getMainModel()).translateToHand();
        }
        
    }
}
