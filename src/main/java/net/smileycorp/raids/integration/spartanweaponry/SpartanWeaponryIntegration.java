package net.smileycorp.raids.integration.spartanweaponry;

import com.google.common.collect.Maps;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.util.NBTHelper;
import com.oblivioussp.spartanweaponry.util.Quaternion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.data.RaidHandler;

import java.util.Map;
import java.util.Random;

public class SpartanWeaponryIntegration {
    
    public static void init() {
        RaidHandler.registerRaidBuffs(ItemCrossbow.class, SpartanWeaponryIntegration::applyCrossbowBuffs);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof ItemCrossbow;
    }
    
    public static ItemStack getCrossbow(Random rand, boolean loot) {
        int r = rand.nextInt(20);
        if (r < 8) return new ItemStack(ItemRegistrySW.crossbowWood);
        if (r < 14) return new ItemStack(ItemRegistrySW.crossbowLeather);
        if (r < 18) return new ItemStack(ItemRegistrySW.crossbowIron);
        return loot ? new ItemStack(ItemRegistrySW.crossbowDiamond) : new ItemStack(ItemRegistrySW.crossbowWood);
    }
    
    public static void setCharged(ItemStack stack, boolean charged) {
        NBTHelper.setBoolean(stack, ItemCrossbow.NBT_IS_LOADED, charged);
    }
    
    public static void shoot(EntityPillager entity, ItemStack stack) {
        ItemCrossbow crossbow = (ItemCrossbow) stack.getItem();
        float velocity = crossbow.getBoltSpeed() * 3;
        int aimTicks = crossbow.getAimTicks(stack);
        int inaccuracy = aimTicks - crossbow.getMaxItemUseDuration(stack);
        float inaccuracyModifier = 0.0f;
        if(crossbow.getMaxItemUseDuration(stack) >= aimTicks) inaccuracy = 0;
        if(inaccuracy != 0) inaccuracyModifier = 10.0f * ((float)inaccuracy / aimTicks);
        boolean spreadshot = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.CROSSBOW_SPREADSHOT, stack) > 0;
        spawnProjectile(stack, entity.world, entity, inaccuracyModifier, 0.0f, velocity);
        if (spreadshot)  {
            spawnProjectile(stack, entity.world, entity, inaccuracyModifier, -10.0f, velocity);
            spawnProjectile(stack, entity.world, entity, inaccuracyModifier, 10.0f, velocity);
        }
        stack.damageItem(spreadshot ? 3 : 1, entity);
        NBTHelper.setBoolean(stack, ItemCrossbow.NBT_IS_LOADED, false);
        NBTHelper.setTagCompound(stack, ItemCrossbow.nbtAmmoStack, new NBTTagCompound());
        entity.playSound(SoundRegistry.CROSSBOW_FIRE, 1, 1 / (entity.getRNG().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
    }
    
    private static void spawnProjectile(ItemStack stack, World world, EntityLiving entity, float inaccuracy, float angle, float velocity) {
        EntityBolt bolt = ItemRegistrySW.bolt.createBolt(world, new ItemStack(ItemRegistrySW.bolt), entity);
        Vec3d lookVec = entity.getLook(1);
        Vec3d vector = new Vec3d(lookVec.x, lookVec.y, lookVec.z);
        if(angle != 0.0f) vector = new Quaternion(calculateEntityViewVector(entity.rotationPitch - 90, entity.rotationYaw), angle, true).transformVector(lookVec);
        bolt.shoot(vector.x, vector.y, vector.z, velocity, inaccuracy);
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        if (j > 0) bolt.setDamage(bolt.getDamage() + j * 0.5 + 0.5);
        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        if (k > 0) bolt.setKnockbackStrength(k);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) bolt.setFire(100);
        bolt.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
        world.spawnEntity(bolt);
    }
    
    private static Vec3d calculateEntityViewVector(float pitch, float yaw) {
        float degToRad = (2.0f * (float)Math.PI) / 360.0f;
        float yawCos = MathHelper.cos(-yaw * degToRad - (float)Math.PI);
        float yawSin = MathHelper.sin(-yaw * degToRad - (float)Math.PI);
        float pitchCos = -MathHelper.cos(-pitch * degToRad - (float)Math.PI);
        float pitchSin = MathHelper.sin(-pitch * degToRad - (float)Math.PI);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
    
    public static void addLoot(LootTable table) {
        table.getPool("raids:outpost_crossbow").addEntry(new LootEntryItem(ItemRegistrySW.crossbowWood, 5, 1, new LootFunction[]{new LootFunctionSWCrossbow()}, new LootCondition[0], "spartanweaponry:crossbow"));
        table.getPool("raids:outpost3").addEntry(new LootEntryItem(ItemRegistrySW.bolt, 2, 1, new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(1, 3))}, new LootCondition[0], "spartanweaponry:bolt"));
    }
    
    public static ItemStack applyCrossbowBuffs(ItemStack stack, EntityLiving entity, Raid raid, int wave, Random rand) {
        if (rand.nextFloat() > raid.getEnchantOdds()) return stack;
        Map<Enchantment, Integer> map = Maps.newHashMap();
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL)) map.put(EnchantmentRegistrySW.CROSSBOW_CHARGE, 2);
        else if (wave > raid.getNumGroups(EnumDifficulty.EASY)) map.put(EnchantmentRegistrySW.CROSSBOW_CHARGE, 1);
        map.put(EnchantmentRegistrySW.CROSSBOW_SPREADSHOT, 1);
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }
    
}
