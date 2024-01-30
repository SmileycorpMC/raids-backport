package net.smileycorp.raids.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;

public class EnchantmentPiercing extends Enchantment {

    public EnchantmentPiercing() {
        super(Rarity.COMMON, RaidsContent.CROSSBOW_ENCHANTMENTS, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        setRegistryName(Constants.loc("piercing"));
        setName(Constants.name("piercing"));
    }

    @Override
    public int getMinEnchantability(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && enchantment != RaidsContent.MULTISHOT;
    }

}
