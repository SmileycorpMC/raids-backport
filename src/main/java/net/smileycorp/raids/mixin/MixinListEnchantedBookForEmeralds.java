package net.smileycorp.raids.mixin;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityVillager.ListEnchantedBookForEmeralds.class)
public class MixinListEnchantedBookForEmeralds {
    
    @Redirect(method = "addMerchantRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/MerchantRecipeList;add(Ljava/lang/Object;)Z"))
    public boolean addMerchantRecipe(MerchantRecipeList instance, Object object) {
        MerchantRecipe recipe = (MerchantRecipe) object;
        return instance.add(new MerchantRecipe(recipe.getSecondItemToBuy(), recipe.getItemToBuy(), recipe.getItemToSell()));
    }
    
}
