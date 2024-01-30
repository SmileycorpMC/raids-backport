package net.smileycorp.raids.mixin;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemBanner.class)
public class MixinItemBanner extends ItemBlock {

    public MixinItemBanner(Block block) {
        super(block);
    }

    @Inject(method = "getSubItems", at = @At("TAIL"), cancellable = true)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items, CallbackInfo callback) {
        if (isInCreativeTab(tab)) items.add(RaidsContent.OMINOUS_BANNER);
    }

}
