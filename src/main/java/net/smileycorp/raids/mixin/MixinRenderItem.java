package net.smileycorp.raids.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.smileycorp.raids.client.ClientHandler;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.futuremc.FutureMCClientIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem {
    
    @Shadow public float zLevel;
    
    //this sucks but is's probably the best way to add support to futuremc's villager gui
    @Inject(at = @At("TAIL"), method = "renderItemOverlays")
    public void renderItemOverlays(FontRenderer fr, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (ModIntegration.FUTUREMC_LOADED && FutureMCClientIntegration.shouldRenderDiscounts(stack)) {
            Minecraft mc = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            mc.renderEngine.bindTexture(ClientHandler.STRIKETHROUGH_TEXTURE);
            GlStateManager.color(1, 1, 1, 1);
            int height = 2;
            int width = 9;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(x + 7, y + 12 + height, zLevel + 300).tex(0, 0.0078125F).endVertex();
            bufferbuilder.pos(x + 7 + width, y + 12 + height, zLevel + 300).tex(0.03515625F, 0.0078125F).endVertex();
            bufferbuilder.pos(x + 7 + width, y + 12, zLevel + 300).tex(0.03515625F, 0).endVertex();
            bufferbuilder.pos(x + 7, y + 12, zLevel + 300).tex(0, 0).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            String s = String.valueOf(FutureMCClientIntegration.getDiscount());
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            FontRenderer fontRenderer = mc.fontRenderer;
            fontRenderer.drawStringWithShadow(s, x + 31 - fontRenderer.getStringWidth(s), y + 9, 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
    }

}
