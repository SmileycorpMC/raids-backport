package net.smileycorp.raids.mixin;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemOverride.class)
public class MixinItemOverride {

    @Shadow @Final private Map<ResourceLocation, Float> mapResourceValues;

    @Inject(method = "matchesItemStack", at = @At("HEAD"), cancellable = true)
    public void matchesItemStack(ItemStack stack, World worldIn, EntityLivingBase livingEntity, CallbackInfoReturnable<Boolean> callback) {
        if (stack.getItem() == RaidsContent.CROSSBOW) {
            boolean ret = true;
            for (Map.Entry<ResourceLocation, Float> entry : mapResourceValues.entrySet())
            {
                IItemPropertyGetter iitempropertygetter = stack.getItem().getPropertyGetter(entry.getKey());
                float f = iitempropertygetter == null ? 0 : iitempropertygetter.apply(stack, worldIn, livingEntity);
                System.out.println(entry + ", " + (iitempropertygetter == null ? "null" : f));
                if (iitempropertygetter == null || f < ((Float)entry.getValue()).floatValue())
                {
                    ret = false;
                }
            }

            callback.setReturnValue(ret);
        }
    }

}
