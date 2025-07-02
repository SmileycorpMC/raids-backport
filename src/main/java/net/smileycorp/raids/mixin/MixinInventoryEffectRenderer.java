package net.smileycorp.raids.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer {
    
    private PotionEffect effect;
    
    @Inject(method = "drawActivePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void raids$drawActivePotionEffects(CallbackInfo ci, int i, int j, int k, Collection collection, int l, Iterator var6, PotionEffect potioneffect, Potion potion) {
        effect = potioneffect;
    }
    
    @WrapOperation(method = "drawActivePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0))
    public int raids$drawActivePotionEffects(FontRenderer instance, String string, float x, float y, int colour, Operation<Integer> original) {
        StringBuilder builder = new StringBuilder(string);
        if (!Loader.isModLoaded("deeperdepths") && effect.getAmplifier() > 3) {
            if ((effect.getPotion() == RaidsContent.BAD_OMEN || effect.getPotion() == RaidsContent.RAID_OMEN ||
                    effect.getPotion() == RaidsContent.HERO_OF_THE_VILLAGE) && effect.getAmplifier() > 4) builder.append(TextFormatting.RED);
            builder.append(" " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1)));
        }
        return original.call(instance, builder.toString(), x, y, colour);
    }
    
}
