package net.smileycorp.raids.integration.futuremc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import thedarkcolour.futuremc.client.gui.GuiVillager;
import thedarkcolour.futuremc.config.FConfig;

import java.util.AbstractMap;
import java.util.Map;

public class FutureMCClientIntegration {
    
    private static Map.Entry<ItemStack, Integer> CACHE = null;
    
    public static boolean shouldRenderDiscounts(ItemStack stack) {
        if (!FConfig.INSTANCE.getVillageAndPillage().newVillagerGui) return false;
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen screen = mc.currentScreen;
        boolean flag = screen instanceof GuiVillager && CACHE != null && CACHE.getKey() == stack && CACHE.getValue() > 0 && stack.getCount() != CACHE.getValue();
        if (!flag) CACHE = null;
        return flag;
    }
    
    public static void setCachedStack(ItemStack stack, int discount) {
        if (!FConfig.INSTANCE.getVillageAndPillage().newVillagerGui) return;
        CACHE = new AbstractMap.SimpleEntry<>(stack, discount);
    }
    
    public static int getDiscount() {
        int discount = CACHE.getValue();
        CACHE = null;
        return discount;
    }
    
}
