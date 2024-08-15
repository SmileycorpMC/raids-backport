package net.smileycorp.raids.mixin;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    public void getSubItems(ItemStack stack, CallbackInfoReturnable<EnumRarity> callback) {
        if (!stack.isItemEqual(RaidsContent.createOminousBanner())) return;
        if (stack.getTagCompound() == null) return;
        callback.setReturnValue(EnumRarity.UNCOMMON);
    }

}
