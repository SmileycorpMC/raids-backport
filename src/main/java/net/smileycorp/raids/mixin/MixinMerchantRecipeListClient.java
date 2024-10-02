package net.smileycorp.raids.mixin;

import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(MerchantRecipeList.class)
public class MixinMerchantRecipeListClient extends ArrayList<MerchantRecipe> {
    
    @Inject(at = @At(value = "TAIL"), method = "readFromBuf", locals = LocalCapture.CAPTURE_FAILHARD)
    private static void raids$readFromBuf(PacketBuffer buffer, CallbackInfoReturnable<MerchantRecipeList> callback, MerchantRecipeList list) {
        int x = 0;
        while (buffer.isReadable()) {
            ((ITradeDiscount)list.get(x)).setDiscountedPrice(buffer.readInt());
            if (x++ >= list.size()) return;
        }
    }

}
