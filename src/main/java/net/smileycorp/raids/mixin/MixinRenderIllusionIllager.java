package net.smileycorp.raids.mixin;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.renderer.entity.RenderIllusionIllager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.monster.EntityMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderIllusionIllager.class)
public abstract class MixinRenderIllusionIllager extends RenderLiving<EntityMob> {
    
    public MixinRenderIllusionIllager(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }
    
    @Inject(at =@At("TAIL"), method = "<init>")
    public void init(RenderManager renderManager, CallbackInfo callbackInfo)  {
        addLayer(new LayerCustomHead(((ModelIllager)getMainModel()).head));
    }
    
}
