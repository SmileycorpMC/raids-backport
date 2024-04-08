package net.smileycorp.raids.integration.spartanweaponry;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;

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
    
}
