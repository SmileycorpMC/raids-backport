package net.smileycorp.raids.client.entity.model;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.common.Constants;

public class RenderRaidsVex extends RenderBiped<EntityVex> {
    
    private static final ResourceLocation TEXTURE = Constants.loc("textures/entity/illager/vex.png");
    private static final ResourceLocation ANGRY_TEXTURE = Constants.loc("textures/entity/illager/vex_charging.png");
    private int modelVersion;
    
    public RenderRaidsVex(RenderManager rm) {
        super(rm, new ModelRaidsVex(), 0.3F);
    }
    
    protected ResourceLocation getEntityTexture(EntityVex entity) {
        return entity.isCharging() ? ANGRY_TEXTURE : TEXTURE;
    }
    
}
