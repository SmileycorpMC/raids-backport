package net.smileycorp.raids.client.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelRaidsVex;
import net.smileycorp.raids.common.Constants;

public class RenderRaidsVex extends RenderLiving<EntityVex> {
    
    private static final ResourceLocation TEXTURE = Constants.loc("textures/entity/illager/vex.png");
    private static final ResourceLocation ANGRY_TEXTURE = Constants.loc("textures/entity/illager/vex_charging.png");
    
    public RenderRaidsVex(RenderManager rm) {
        super(rm, new ModelRaidsVex(), 0.3f);
        addLayer(new LayerHeldItemVex(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityVex vex) {
        return vex.isCharging() ? ANGRY_TEXTURE : TEXTURE;
    }
    
    @Override
    public void doRender(EntityVex vex, double x, double y, double z, float yaw, float pt) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        super.doRender(vex, x, y, z, yaw, pt);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }
    
    @Override
    public void setLightmap(EntityVex vex) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15, 15);
    }
    
    private static class LayerHeldItemVex extends LayerHeldItem {
        public LayerHeldItemVex(RenderRaidsVex renderer) {
            super(renderer);
        }
        
        @Override
        protected void translateToHand(EnumHandSide side) {
            ((ModelRaidsVex)livingEntityRenderer.getMainModel()).translateToHand(side);
        }
        
    }
}
