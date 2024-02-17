package net.smileycorp.raids.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.ArrayList;

@Mixin(MerchantRecipeList.class)
public class MixinMerchantRecipeListClient extends ArrayList<MerchantRecipe> {
    
    @Inject(at = @At(value = "HEAD"), method = "readFromBuf", cancellable = true)
    private static void readFromBuf(PacketBuffer buffer, CallbackInfoReturnable<MerchantRecipeList> callback) throws IOException {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i = buffer.readByte() & 255;
        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = buffer.readItemStack();
            ItemStack itemstack1 = buffer.readItemStack();
            ItemStack itemstack2 = ItemStack.EMPTY;
            if (buffer.readBoolean()) itemstack2 = buffer.readItemStack();
            boolean flag = buffer.readBoolean();
            int k = buffer.readInt();
            int l = buffer.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l);
            if (flag)merchantrecipe.compensateToolUses();
            ((ITradeDiscount)merchantrecipe).setDiscountedPrice(buffer.readInt());
            merchantrecipelist.add(merchantrecipe);
        }
        callback.setReturnValue(merchantrecipelist);
    }

}
