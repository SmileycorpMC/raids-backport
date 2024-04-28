package net.smileycorp.raids.integration;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.integration.crossbow.CrossbowIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;
import net.smileycorp.raids.integration.spartanweaponry.SpartanWeaponryIntegration;
import net.smileycorp.raids.integration.tconstruct.TinkersConstructIntegration;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ModIntegration {
    public static boolean CROSSBOWS_BACKPORT_LOADED = Loader.isModLoaded("crossbows");
    public static boolean CROSSBOW_LOADED = Loader.isModLoaded("crossbow");
    public static boolean SPARTAN_LOADED = Loader.isModLoaded("spartanweaponry");
    public static boolean TINKERS_LOADED = Loader.isModLoaded("tconstruct");
    public static boolean FUTUREMC_LOADED = Loader.isModLoaded("futuremc");
    public static boolean TEKTOPIA_LOADED = Loader.isModLoaded("tektopia");
    
    public static boolean HAS_CROSSBOW_MOD = CROSSBOWS_BACKPORT_LOADED || CROSSBOW_LOADED || SPARTAN_LOADED || TINKERS_LOADED;
    
    private static List<Function<Random, ItemStack>> ITEM_SUPPLIERS = null;
    
    public static void init() {
        if (CROSSBOWS_BACKPORT_LOADED) CrossbowsBackportIntegration.init();
        if (CROSSBOW_LOADED) CrossbowIntegration.init();
        if (SPARTAN_LOADED) SpartanWeaponryIntegration.init();
        if (TINKERS_LOADED) TinkersConstructIntegration.init();
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        if (CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(stack)) return true;
        if (CROSSBOW_LOADED && CrossbowIntegration.isCrossbow(stack)) return true;
        if (SPARTAN_LOADED && SpartanWeaponryIntegration.isCrossbow(stack)) return true;
        if (TINKERS_LOADED && TinkersConstructIntegration.isCrossbow(stack)) return true;
        return false;
    }
    
    public static ItemStack getPillagerItem(Random rand) {
        if (!HAS_CROSSBOW_MOD) return new ItemStack(Items.BOW);
        if (ITEM_SUPPLIERS == null) {
            ITEM_SUPPLIERS = Lists.newArrayList();
            if (CROSSBOWS_BACKPORT_LOADED && EntityConfig.crossbowsBackportCrossbows) ITEM_SUPPLIERS.add(r -> CrossbowsBackportIntegration.getCrossbow());
            if (CROSSBOW_LOADED && EntityConfig.crossbowCrossbows) ITEM_SUPPLIERS.add(r -> CrossbowIntegration.getCrossbow());
            if (SPARTAN_LOADED && EntityConfig.spartansWeaponryCrossbows) ITEM_SUPPLIERS.add(r -> SpartanWeaponryIntegration.getCrossbow(r, false));
            if (TINKERS_LOADED && EntityConfig.tinkersConstructCrossbows) ITEM_SUPPLIERS.add(r -> TinkersConstructIntegration.getCrossbow(r, false));
        }
        return ITEM_SUPPLIERS.isEmpty() ? new ItemStack(Items.BOW) : ITEM_SUPPLIERS.get(rand.nextInt(ITEM_SUPPLIERS.size())).apply(rand);
    }
    
    public static void setCharged(ItemStack stack, boolean charged) {
        if (CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(stack)) CrossbowsBackportIntegration.setCharged(stack, charged);
        if (SPARTAN_LOADED && SpartanWeaponryIntegration.isCrossbow(stack)) SpartanWeaponryIntegration.setCharged(stack, charged);
        if (TINKERS_LOADED && TinkersConstructIntegration.isCrossbow(stack)) TinkersConstructIntegration.setCharged(stack, charged);
    }
    
    public static void performShooting(EntityPillager entity, ItemStack stack, float velocity) {
        if (CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(stack)) CrossbowsBackportIntegration.shoot(entity, stack, velocity);
        if (CROSSBOW_LOADED && CrossbowIntegration.isCrossbow(stack)) CrossbowIntegration.shoot(entity, stack, velocity);
        if (SPARTAN_LOADED && SpartanWeaponryIntegration.isCrossbow(stack)) SpartanWeaponryIntegration.shoot(entity, stack);
        if (TINKERS_LOADED && TinkersConstructIntegration.isCrossbow(stack)) TinkersConstructIntegration.shoot(entity, stack);
    }
    
    public static boolean isCharged(ItemStack stack, EntityPillager entity) {
        if (CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(stack)) return entity.getItemInUseCount() < -stack.getMaxItemUseDuration();
        if (CROSSBOW_LOADED && CrossbowIntegration.isCrossbow(stack)) return entity.getItemInUseCount() < -stack.getMaxItemUseDuration();
        if (SPARTAN_LOADED && SpartanWeaponryIntegration.isCrossbow(stack)) return entity.getItemInUseCount() < -stack.getMaxItemUseDuration();
        if (TINKERS_LOADED && TinkersConstructIntegration.isCrossbow(stack)) return TinkersConstructIntegration.isCharged(stack, entity);
        return false;
    }
    
    public static float getChargeAmount(ItemStack stack, EntityLivingBase entity) {
        if (CROSSBOWS_BACKPORT_LOADED && CrossbowsBackportIntegration.isCrossbow(stack)) return CrossbowsBackportIntegration.getChargeAmount(stack, entity);
        if (CROSSBOW_LOADED && CrossbowIntegration.isCrossbow(stack)) return CrossbowIntegration.getChargeAmount(stack, entity);
        if (TINKERS_LOADED && TinkersConstructIntegration.isCrossbow(stack)) return TinkersConstructIntegration.getChargeAmount(stack, entity);
        return (float) -entity.getItemInUseCount() / (float) stack.getMaxItemUseDuration();
    }
    
}
