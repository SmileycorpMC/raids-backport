package net.smileycorp.raids.mixin;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.smileycorp.raids.common.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    public void raids$getRarity(ItemStack stack, CallbackInfoReturnable<EnumRarity> callback) {
        if (!stack.isItemEqual(Constants.ominousBanner())) return;
        if (stack.getTagCompound() == null) return;
        callback.setReturnValue(EnumRarity.UNCOMMON);
    }

}
