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
        int i = rand.nextInt(10);
        return i == 0 ? TinkersConstructIntegration.generateRandomCrossbow(rand) :
                i < 4 ? TinkersConstructIntegration.getIronCrossbow(rand) : TinkersConstructIntegration.getWoodCrossbow();
    }
    
}
