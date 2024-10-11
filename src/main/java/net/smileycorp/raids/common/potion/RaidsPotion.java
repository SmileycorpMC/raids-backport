package net.smileycorp.raids.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.common.Constants;

public class RaidsPotion extends Potion {
    
    private final ResourceLocation texture;
    
    public RaidsPotion(boolean isBad, int colour, String name) {
        super(isBad, colour);
        setPotionName("effect.raids." + name);
        setRegistryName(Constants.loc(name));
        texture = Constants.loc("textures/mob_effect/" + name + ".png");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        renderEffect(effect, x + 6, y + 7, 1);
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
