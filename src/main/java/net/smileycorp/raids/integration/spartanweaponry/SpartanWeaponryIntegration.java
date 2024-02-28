package net.smileycorp.raids.integration.spartanweaponry;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class SpartanWeaponryIntegration {
    
    public static void addLoot(LootPool pool) {
        pool.addEntry(new LootEntryItem(ItemRegistrySW.crossbowWood, 5, 1, new LootFunction[]{new LootFunctionSWCrossbow()}, new LootCondition[0], "spartanweaponry:crossbow"));
    }
    
    public static ItemStack getRandomCrossbow(Random rand) {
        int r = rand.nextInt(20);
        if (r < 8) return new ItemStack(ItemRegistrySW.crossbowWood);
        if (r < 14) return new ItemStack(ItemRegistrySW.crossbowLeather);
        if (r < 18) return new ItemStack(ItemRegistrySW.crossbowIron);
        return new ItemStack(ItemRegistrySW.crossbowDiamond);
    }
    
}
