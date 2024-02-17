package net.smileycorp.raids.mixin;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantRecipe.class)
public class MixinMerchantRecipe implements ITradeDiscount {
    
    @Shadow private ItemStack itemToBuy;
    @Shadow private ItemStack secondItemToBuy;
    private int discountedPrice;
    
    @Override
    public int getDiscountedPrice() {
        return discountedPrice;
    }
    
    @Override
    public void setDiscountedPrice(int price) {
        discountedPrice = MathUtils.clamp(itemToBuy.getCount() + price, 1, 64);
    }
    
    @Override
    public boolean hasDiscount() {
        return discountedPrice != 0;
    }
    
    @Inject(at = @At("TAIL"), method = "readFromTags")
    public void readFromTags(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("discountedPrice")) discountedPrice = nbt.getInteger("discountedPrice");
        if (itemToBuy != null && itemToBuy.getItem() == Items.BOOK && secondItemToBuy != null && secondItemToBuy.getItem() == Items.EMERALD) {
            ItemStack stack = itemToBuy;
            itemToBuy = secondItemToBuy;
            secondItemToBuy = stack;
        }
    }
    
    @Inject(at = @At("RETURN"), method = "writeToTags")
    public void writeToTags(CallbackInfoReturnable<NBTTagCompound> callback) {
        callback.getReturnValue().setInteger("discountedPrice", discountedPrice);
    }
    
}
