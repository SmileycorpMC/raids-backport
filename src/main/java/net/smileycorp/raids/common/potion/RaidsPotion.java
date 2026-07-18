package net.smileycorp.raids.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.common.Constants;

public class RaidsPotion extends Potion {
    
    protected final ResourceLocation texture;
    protected final int maxLevel;
    
    public RaidsPotion(String name, boolean isBad, int colour, int maxLevel) {
        super(isBad, colour);
        setPotionName("effect.raids." + name);
        setRegistryName(Constants.loc(name));
        texture = Constants.loc("textures/mob_effect/" + name + ".png");
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        renderEffect(effect, x + 6, y + 7, 1);
        if (shouldRenderInvText(effect)) return;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        StringBuilder builder = new StringBuilder(I18n.format(getName()));
        builder.append(" ");
        if (effect.getAmplifier() > maxLevel) builder.append(isBadEffect() ? TextFormatting.RED : TextFormatting.GREEN);
        builder.append(I18n.format("enchantment.level." + (effect.getAmplifier() + 1)));
        fontRenderer.drawStringWithShadow(builder.toString(), (float)(x + 10 + 18), (float)(y + 6), 16777215);
        fontRenderer.drawStringWithShadow(Potion.getPotionDurationString(effect, 1), x + 10 + 18, y + 6 + 10, 8355711);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        renderEffect(effect, x + 3, y + 3, alpha);
    }
    
    @SideOnly(Side.CLIENT)
    protected void renderEffect(PotionEffect effect, int x, int y, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, alpha);
        Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(effect));
        Gui.drawScaledCustomSizeModalRect(x, y, 0, 0 , 18, 18, 18, 18, 18, 18);
        GlStateManager.popMatrix();
    }
    
    protected ResourceLocation getTexture(PotionEffect effect) {
        return texture;
    }
    
}
