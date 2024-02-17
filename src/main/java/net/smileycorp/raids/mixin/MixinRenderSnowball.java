package net.smileycorp.raids.mixin;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.smileycorp.raids.common.interfaces.IFireworksProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSnowball.class)
public class MixinRenderSnowball {

    @Inject(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"), cancellable = true)
    public void renderItem(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entity instanceof IFireworksProjectile && ((IFireworksProjectile) entity).isShotAtAngle()) {
            GlStateManager.rotate(180.0F, 0, 0, 1);
            GlStateManager.rotate(180.0F, 0, 1, 0);
            GlStateManager.rotate(90.0F, 1, 0, 0);
        }
    }

}
