package net.smileycorp.raids.integration.tconstruct;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.smileycorp.raids.common.RaidsLogger;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.item.ItemMaterials;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LootFunctionTConCrossbow extends LootFunction {
    
    protected LootFunctionTConCrossbow() {
        super(new LootCondition[]{});
    }
    
    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        return TinkersConstructIntegration.generateRandomCrossbow(rand);
    }
    
}
