package net.smileycorp.raids.integration.tconstruct;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.raid.Raid;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

import java.util.List;
import java.util.Random;

public class TinkersConstructIntegration {
    public static void addLoot(LootPool pool) {
        pool.addEntry(new LootEntryItem(TinkerRangedWeapons.crossBow, 3, 1, new LootFunction[]{new LootFunctionTConCrossbow()}, new LootCondition[0], "tconstruct:crossbow"));
    }
    
    public static void applyRaidBuffs(EntityPillager entity, Raid raid, int wave) {
        ItemStack itemstack = getWoodCrossbow();
        if (wave > raid.getNumGroups(EnumDifficulty.EASY)) itemstack = getIronCrossbow(entity.getRNG());
        if (wave > raid.getNumGroups(EnumDifficulty.NORMAL) || raid.getBadOmenLevel() > 2) {
            int h = entity.getRNG().nextInt(wave * raid.getBadOmenLevel() * 3);
            if (h > 150) h = 150;
            while (h > 0) {
                ItemStack redstone = new ItemStack(Items.REDSTONE, Math.min(64, h));
                int consumed = redstone.getCount();
                try {
                    ToolBuilder.tryModifyTool(NonNullList.from(redstone), itemstack, true);
                    h -= (consumed - redstone.getCount());
                } catch (Exception e) {
                    break;
                }
            }
            
        }
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
    }
    
    public static ItemStack getWoodCrossbow() {
        List<Material> parts = Lists.newArrayList(TinkerMaterials.wood, TinkerMaterials.wood, TinkerMaterials.wood, TinkerMaterials.string);
        return TinkerRangedWeapons.crossBow.buildItem(parts);
    }
    
    public static ItemStack getIronCrossbow(Random rand) {
        List<Material> parts = Lists.newArrayList(TinkerMaterials.wood, rand.nextInt(0) == 0 ? TinkerMaterials.iron : TinkerMaterials.wood,
                rand.nextInt(0) == 0 ? TinkerMaterials.iron : TinkerMaterials.wood, TinkerMaterials.string);
        return TinkerRangedWeapons.crossBow.buildItem(parts);
    }
    
    public static ItemStack generateRandomCrossbow(Random rand) {
        List<Material> parts = Lists.newArrayList();
        for (ToolPart part : Lists.newArrayList(TinkerTools.toughToolRod, TinkerTools.bowLimb, TinkerTools.toughBinding, TinkerTools.bowString)) {
            ImmutableList<Material> materials = (ImmutableList<Material>) TinkerRegistry.getAllMaterials();
            Material material = materials.get(rand.nextInt(materials.size()));
            while (!part.canUseMaterial(material)) material = materials.get(rand.nextInt(materials.size()));
            parts.add(material);
        }
        return TinkerRangedWeapons.crossBow.buildItem(parts);
    }
    
}
