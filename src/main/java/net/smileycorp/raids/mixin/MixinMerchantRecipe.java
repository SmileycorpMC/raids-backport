package net.smileycorp.raids.mixin;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.futuremc.FutureMCClientIntegration;
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
        discountedPrice = MathUtils.clamp(price, 0, 64);
    }
    
    @Override
    public boolean hasDiscount() {
        return discountedPrice != itemToBuy.getCount() && discountedPrice > 0;
    }
    
    @Inject(at = @At("TAIL"), method = "getItemToBuy")
    public void getItemToBuy(CallbackInfoReturnable<ItemStack> callback) {
        if (ModIntegration.FUTUREMC_LOADED && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            FutureMCClientIntegration.setCachedStack(callback.getReturnValue(), discountedPrice);
    }
    
    @Inject(at = @At("TAIL"), method = "readFromTags")
    public void readFromTags(NBTTagCompound nbt, CallbackInfo callback) {
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
