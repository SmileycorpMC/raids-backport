package net.smileycorp.raids.client.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.raids.client.entity.model.ModelRavager;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.entities.EntityRavager;

public class RenderRavager extends RenderLiving<EntityRavager> {
	
	private static final ResourceLocation texture = Constants.loc("textures/entity/illager/ravager.png");

    public RenderRavager(RenderManager rm) {
        super(rm, new ModelRavager(), 1.1f);
    }
    
    @Override
	protected ResourceLocation getEntityTexture(EntityRavager entity) {
        return texture;
    }
    
}
