package net.smileycorp.raids.integration.crossbow;

import com.google.common.collect.Maps;
import git.jbredwards.crossbow.api.ICrossbow;
import git.jbredwards.crossbow.mod.common.capability.ICrossbowProjectiles;
import git.jbredwards.crossbow.mod.common.init.CrossbowEnchantments;
import git.jbredwards.crossbow.mod.common.init.CrossbowItems;
import git.jbredwards.crossbow.mod.common.item.ItemCrossbow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.util.Map;
import java.util.Random;

public class CrossbowIntegration {
    
    public static void init() {
        RaidHandler.registerRaidBuffs(ICrossbow.class, CrossbowIntegration::applyCrossbowBuffs);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof ICrossbow;
    }
    
    public static ItemStack getCrossbow() {
        return new ItemStack(CrossbowItems.CROSSBOW);
    }
    
    public static void shoot(EntityPillager entity, ItemStack stack, float velocity) {
        ItemCrossbow.shootAll(entity.world, entity, stack, ICrossbowProjectiles.get(stack), velocity, 14 - entity.getEntityWorld().getDifficulty().getDifficultyId() * 4);
    }
    
    public static void addLoot(LootTable table) {
        table.getPool("raids:outpost_crossbow").addEntry(new LootEntryItem(CrossbowItems.CROSSBOW, 7, 1, new LootFunction[0], new LootCondition[0], "crossbow:crossbow"));
    }
    
    public static ItemStack applyCrossbowBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
        if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
        Map<Enchantment, Integer> map = Maps.newHashMap();
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) map.put(CrossbowEnchantments.QUICK_CHARGE, 2);
        else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(CrossbowEnchantments.QUICK_CHARGE, 1);
        map.put(CrossbowEnchantments.MULTISHOT, 1);
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }
    
    public static float getChargeAmount(ItemStack stack, EntityLivingBase entity) {
        float f = CrossbowItems.CROSSBOW.getPullTime(stack);;
        float f1 = -MathHelper.clamp((float)entity.getItemInUseCount(), 0.0F, f);
        return f1/f;
    }
    
    
}
