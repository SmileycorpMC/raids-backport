package net.smileycorp.raids.mixin;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.IMerchant;
import net.minecraft.inventory.Container;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMerchant.class)
public abstract class MixinGuiMerchant extends GuiContainer {
    
    @Shadow @Final private IMerchant merchant;
    
    @Shadow private int selectedMerchantRecipe;
    
    public MixinGuiMerchant(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V"), method = "drawScreen")
    public void drawScreen$renderItemAndEffectIntoGUI(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        itemRender.zLevel = 0;
    }
    
    @Inject(at = @At("TAIL"), method = "drawScreen")
    public void drawScreen$TAIL(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        MerchantRecipeList recipes = merchant.getRecipes(mc.player);
        if (recipes == null) return;
        if (recipes.isEmpty()) return;
        ITradeDiscount trade = (ITradeDiscount) recipes.get(selectedMerchantRecipe);
        if (trade.hasDiscount()) {
            int count = trade.getDiscountedPrice();
            int x = ((width - xSize) / 2) + 36;
            int y = ((height - ySize) / 2) + 24;
            GlStateManager.pushMatrix();
            mc.renderEngine.bindTexture(ClientHandler.STRIKETHROUGH_TEXTURE);
            GlStateManager.color(1, 1, 1, 1);
            drawTexturedModalRect(x + 7, y + 12, 0, 0 , 9, 2);
            GlStateManager.popMatrix();
            String s = String.valueOf(count);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            fontRenderer.drawStringWithShadow(s, x + 17 - fontRenderer.getStringWidth(s), y + 19, 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
    }
    
}
