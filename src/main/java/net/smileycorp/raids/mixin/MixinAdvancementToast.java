package net.smileycorp.raids.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.Constants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementToast.class)
public class MixinAdvancementToast {

    @Shadow @Final private Advancement advancement;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;II)V"), method = "draw")
    public void raids$draw$renderItemAndEffectIntoGUI(GuiToast toast, long p_193653_2_, CallbackInfoReturnable<IToast.Visibility> callback) {
        if (!advancement.getId().equals(Constants.OVERLEVELED)) return;
        ClientHandler.renderFire(8, 7, 1);
    }
    
}
