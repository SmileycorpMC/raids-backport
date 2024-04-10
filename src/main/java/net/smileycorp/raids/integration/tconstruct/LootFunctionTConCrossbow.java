package net.smileycorp.raids.integration.tconstruct;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class LootFunctionTConCrossbow extends LootFunction {
    
    protected LootFunctionTConCrossbow() {
        super(new LootCondition[]{});
    }
    
    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        return TinkersConstructIntegration.getCrossbow(rand, true);
    }
    
}
