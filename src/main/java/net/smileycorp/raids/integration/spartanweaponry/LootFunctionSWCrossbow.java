package net.smileycorp.raids.integration.spartanweaponry;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class LootFunctionSWCrossbow extends LootFunction {
    
    protected LootFunctionSWCrossbow() {
        super(new LootCondition[]{});
    }
    
    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        return SpartanWeaponryIntegration.getCrossbow(rand, true);
    }
    
}
