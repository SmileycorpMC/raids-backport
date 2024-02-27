package net.smileycorp.raids.integration.crossbows;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.ai.EntityAIAttackRangedCrossbow;
import net.smileycorp.crossbows.common.entities.ICrossbowArrow;
import net.smileycorp.crossbows.common.entities.IFireworksProjectile;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.raid.Raid;

import java.util.Map;

public class CrossbowsIntegration {
    
    public static void addTask(EntityPillager entity) {
        entity.tasks.addTask(3, new EntityAIAttackRangedCrossbow<EntityPillager>(entity, 1.0D, 20,
                entity::setChargingCrossbow, entity::onCrossbowAttackPerformed));
    }
    
    public static ItemStack getCrossbow() {
        return new ItemStack(CrossbowsContent.CROSSBOW);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() == CrossbowsContent.CROSSBOW;
    }
    
    public static boolean isCrossbowProjectile(Entity entity) {
        return entity instanceof ICrossbowArrow && ((ICrossbowArrow)entity).shotFromCrossbow();
    }
    
    public static void applyRaidBuffs(EntityPillager entity, Raid raid, int wave) {
        ItemStack itemstack = new ItemStack(CrossbowsContent.CROSSBOW);
        Map<Enchantment, Integer> map = Maps.newHashMap();
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) map.put(CrossbowsContent.QUICK_CHARGE, 2);
        else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(CrossbowsContent.QUICK_CHARGE, 1);
        map.put(CrossbowsContent.MULTISHOT, 1);
        EnchantmentHelper.setEnchantments(map, itemstack);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
    }
    
    public static void setOwner(EntityFireworkRocket firework, EntityLivingBase owner) {
        ((IFireworksProjectile)firework).setOwner(owner);
    }
    
}
