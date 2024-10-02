package net.smileycorp.raids.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(MerchantRecipeList.class)
public class MixinMerchantRecipeList extends ArrayList<MerchantRecipe> {
    
    @Redirect(method = "canRecipeBeUsed", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/MerchantRecipe;getItemToBuy()Lnet/minecraft/item/ItemStack;"))
    public ItemStack raids$canRecipeBeUsed$getItemToBuy(MerchantRecipe instance) {
        if (!((ITradeDiscount)instance).hasDiscount()) return instance.getItemToBuy();
        ItemStack stack = instance.getItemToBuy().copy();
        stack.setCount(((ITradeDiscount)instance).getDiscountedPrice());
        return stack;
    }
    
    @Inject(at = @At(value = "HEAD"), method = "writeToBuf", cancellable = true)
    public void raids$writeToBuf(PacketBuffer buffer, CallbackInfo callback) {
        buffer.writeByte((byte)(size() & 255));
        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = get(i);
            buffer.writeItemStack(merchantrecipe.getItemToBuy());
            buffer.writeItemStack(merchantrecipe.getItemToSell());
            ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            buffer.writeBoolean(!itemstack.isEmpty());
            if (!itemstack.isEmpty()) buffer.writeItemStack(itemstack);
            buffer.writeBoolean(merchantrecipe.isRecipeDisabled());
            buffer.writeInt(merchantrecipe.getToolUses());
            buffer.writeInt(merchantrecipe.getMaxTradeUses());
        }
        for (MerchantRecipe recipe : this) buffer.writeInt(((ITradeDiscount)recipe).getDiscountedPrice());
        callback.cancel();
    }

}
