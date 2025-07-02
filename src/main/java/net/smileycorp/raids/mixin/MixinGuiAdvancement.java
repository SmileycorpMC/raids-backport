package net.smileycorp.raids.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.advancements.GuiAdvancement;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.Constants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAdvancement.class)
public class MixinGuiAdvancement {

    @Shadow @Final private Advancement advancement;

    @Shadow @Final private int x;

    @Shadow @Final private int y;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;II)V"), method = "draw")
    public void raids$draw$renderItemAndEffectIntoGUI(int x, int y, CallbackInfo callback) {
        if (!advancement.getId().equals(Constants.OVERLEVELED)) return;
        ClientHandler.renderFire(x + this.x + 8, y + this.y + 4, 1);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;II)V"), method = "drawHover")
    public void raids$drawHover$renderItemAndEffectIntoGUI(int x, int y, float p_191821_3_, int p_191821_4_, int p_191821_5_, CallbackInfo callback) {
        if (!advancement.getId().equals(Constants.OVERLEVELED)) return;
        ClientHandler.renderFire(x + this.x + 8, y + this.y + 4, 1);
    }
    
}
