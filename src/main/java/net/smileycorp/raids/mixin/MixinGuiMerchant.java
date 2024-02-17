package net.smileycorp.raids.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.IMerchant;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMerchant.class)
public abstract class MixinGuiMerchant extends GuiContainer {
    
    private static final ResourceLocation STRIKETHROUGH = Constants.loc("textures/gui/villager_strikethrough.png");
    
    @Shadow @Final private IMerchant merchant;
    
    @Shadow private int selectedMerchantRecipe;
    
    public MixinGuiMerchant(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
    
    @Inject(at = @At("TAIL"), method = "drawScreen")
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        MerchantRecipeList recipes = merchant.getRecipes(mc.player);
        if (recipes != null &! recipes.isEmpty()) {
            ITradeDiscount trade = (ITradeDiscount) recipes.get(selectedMerchantRecipe);
            if (trade.hasDiscount()) {
                int count = trade.getDiscountedPrice();
                int x = (width - xSize) / 2 + 36;
                int y = (height - ySize) / 2 + 24;
                GlStateManager.pushMatrix();
                mc.renderEngine.bindTexture(STRIKETHROUGH);
                Gui.drawScaledCustomSizeModalRect(x + 7, y + 36, 0, 0 , 9, 2, 9, 2, 9, 2);
                GlStateManager.popMatrix();
                String s = String.valueOf(count);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fontRenderer.drawStringWithShadow(s, x + 31 - fontRenderer.getStringWidth(s), y + 9, 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
            }
        }
    }
    
}
