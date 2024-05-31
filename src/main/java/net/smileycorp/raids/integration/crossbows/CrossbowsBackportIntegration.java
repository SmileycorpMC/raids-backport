package net.smileycorp.raids.integration.crossbows;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.entities.ICrossbowArrow;
import net.smileycorp.crossbows.common.entities.IFireworksProjectile;
import net.smileycorp.crossbows.common.item.ItemCrossbow;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.util.Map;
import java.util.Random;

public class CrossbowsBackportIntegration {
    
    public static void init() {
        RaidHandler.registerRaidBuffs(ItemCrossbow.class, CrossbowsBackportIntegration::applyCrossbowBuffs);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof ItemCrossbow;
    }
    
    public static ItemStack getCrossbow() {
        return new ItemStack(CrossbowsContent.CROSSBOW);
    }
    
    public static void setCharged(ItemStack stack, boolean charged) {
        if (charged == false) ItemCrossbow.setCharged(stack, charged);
    }
    
    public static void shoot(EntityPillager entity, ItemStack stack, float velocity) {
        ItemCrossbow.performShooting(entity.world, entity, stack, velocity, 14 - entity.getEntityWorld().getDifficulty().getDifficultyId() * 4);
    }
    
    public static void addLoot(LootTable table) {
        table.getPool("raids:outpost_crossbow").addEntry(new LootEntryItem(CrossbowsContent.CROSSBOW, 7, 1, new LootFunction[0], new LootCondition[0], "crossbows:crossbow"));
    }
    
    public static ItemStack applyCrossbowBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
        if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
        Map<Enchantment, Integer> map = Maps.newHashMap();
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) map.put(CrossbowsContent.QUICK_CHARGE, 2);
        else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(CrossbowsContent.QUICK_CHARGE, 1);
        map.put(CrossbowsContent.MULTISHOT, 1);
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }
    
    public static boolean isCrossbowProjectile(Entity entity) {
        return entity instanceof ICrossbowArrow && ((ICrossbowArrow)entity).shotFromCrossbow();
    }
    
    public static void setOwner(EntityFireworkRocket firework, EntityLivingBase owner) {
        ((IFireworksProjectile)firework).setOwner(owner);
    }
    
    public static float getChargeAmount(ItemStack stack, EntityLivingBase entity) {
        float f = ItemCrossbow.getChargeDuration(stack);
        float f1 = -MathHelper.clamp((float)entity.getItemInUseCount(), 0.0F, f);
        return f1/f;
    }
    
}
