package net.smileycorp.raids.integration.spartanweaponry;

import com.google.common.collect.Maps;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.raids.common.CommonProxy;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.util.Map;
import java.util.Random;

public class SpartanWeaponryIntegration {
    
    public static void addLoot(LootTable table) {
        table.getPool("raids:outpost_crossbow").addEntry(new LootEntryItem(ItemRegistrySW.crossbowWood, 5, 1, new LootFunction[]{new LootFunctionSWCrossbow()}, new LootCondition[0], "spartanweaponry:crossbow"));
        table.getPool("raids:outpost3").addEntry(new LootEntryItem(ItemRegistrySW.bolt, 2, 1, new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(1, 3))}, new LootCondition[0], "spartanweaponry:bolt"));
    }
    
    public static ItemStack getRandomCrossbow(Random rand) {
        int r = rand.nextInt(20);
        if (r < 8) return new ItemStack(ItemRegistrySW.crossbowWood);
        if (r < 14) return new ItemStack(ItemRegistrySW.crossbowLeather);
        if (r < 18) return new ItemStack(ItemRegistrySW.crossbowIron);
        return new ItemStack(ItemRegistrySW.crossbowDiamond);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof ItemCrossbow;
    }
    
    public static ItemStack applyCrossbowBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
        if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
        if (wave > raid.getNumGroups(EnumDifficulty.EASY) && rand.nextInt(10) == 0) stack = new ItemStack(ItemRegistrySW.crossbowIron);
        else if (rand.nextInt(5) < 2) stack = new ItemStack(ItemRegistrySW.crossbowLeather);
        Map<Enchantment, Integer> map = Maps.newHashMap();
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) map.put(CrossbowsContent.QUICK_CHARGE, 2);
        else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(CrossbowsContent.QUICK_CHARGE, 1);
        map.put(CrossbowsContent.MULTISHOT, 1);
        EnchantmentHelper.setEnchantments(map, stack);
        return CommonProxy.applyBowBuffs(stack, entity, raid, wave, rand);
    }
    
    public static void init() {
        RaidHandler.registerRaidBuffs(ItemCrossbow.class, SpartanWeaponryIntegration::applyCrossbowBuffs);
    }
    
}
