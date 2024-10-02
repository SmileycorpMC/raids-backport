package net.smileycorp.raids.mixin;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.WorldDataRaids;
import net.smileycorp.raids.common.util.accessors.IVillager;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVillager.class)
public abstract class MixinEntityVillager extends EntityAgeable implements IVillager {
    
    
    @Shadow public abstract BlockPos getPos();
    
    @Shadow public abstract float getEyeHeight();
    
    @Shadow private int careerId;
    
    public MixinEntityVillager(World p_i1578_1_) {
        super(p_i1578_1_);
    }
    
    @Inject(at = @At("HEAD"), method = "updateAITasks", cancellable = true)
    protected void raids$updateAITasks(CallbackInfo callback) {
        if (world.isRemote) return;
        Raid raid = WorldDataRaids.getData((WorldServer) world).getRaidAt(getPos());
        if (raid == null) return;
        if (raid.isVictory() && rand.nextInt(200) == 0) {
            if (world.getHeight((int) posX, (int) posZ) > posY) return;
            ItemStack stack = Constants.villagerFirework(rand);
            EntityFireworkRocket firework = new EntityFireworkRocket(world, posX, posY + getEyeHeight(), posZ, stack );
            if (ModIntegration.CROSSBOWS_BACKPORT_LOADED) CrossbowsBackportIntegration.setOwner(firework, this);
            world.spawnEntity(firework);
            firework.motionY = 0.01;
        }
    }
    
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"), method = "useRecipe")
    public int raids$useRecipe(ItemStack instance, MerchantRecipe recipe) {
        return ((ITradeDiscount)recipe).hasDiscount() ? ((ITradeDiscount) recipe).getDiscountedPrice() : instance.getCount();
    }
    
    @Override
    public int getCareer() {
        return careerId;
    }
    
}
