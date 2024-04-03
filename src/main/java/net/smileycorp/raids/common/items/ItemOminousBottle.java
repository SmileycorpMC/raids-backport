package net.smileycorp.raids.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;

public class ItemOminousBottle extends Item {
    public ItemOminousBottle() {
        setUnlocalizedName(Constants.name("Ominous_Bottle"));
        setRegistryName(Constants.loc("Ominous_Bottle"));
        setCreativeTab(CreativeTabs.BREWING);
        setHasSubtypes(true);
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                player.addPotionEffect(new PotionEffect(RaidsContent.BAD_OMEN, 6000, stack.getMetadata()));
                if (player instanceof EntityPlayerMP) CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player, stack);
                if (stack.getCount()>1 &! player.isCreative()) if (!player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE)))
                    player.dropItem(Items.GLASS_BOTTLE, 1);
            }
            stack.shrink(1);
            return stack.getCount() < 1 ? new ItemStack(Items.GLASS_BOTTLE) : stack;
    }
    
    @Override
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> itemList) {
        if(!isInCreativeTab(tabs)) return;
        for(int counter = 0; counter < 5; counter++) {
            itemList.add(new ItemStack(this, 1, counter));
        }
    }
    
}
