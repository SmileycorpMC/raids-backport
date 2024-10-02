package net.smileycorp.raids.mixin;

import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlotMerchantResult.class)
public class MixinSlotMerchantResult {
    
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/village/MerchantRecipe;getItemToBuy()Lnet/minecraft/item/ItemStack;"), method = "doTrade")
    public ItemStack raids$getItemToBuy(MerchantRecipe instance) {
        if (!((ITradeDiscount)instance).hasDiscount()) return instance.getItemToBuy();
        ItemStack stack = instance.getItemToBuy().copy();
        stack.setCount(((ITradeDiscount)instance).getDiscountedPrice());
        return stack;
    }
    
}
