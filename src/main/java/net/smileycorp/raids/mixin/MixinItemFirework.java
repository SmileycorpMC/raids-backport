package net.smileycorp.raids.mixin;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.raids.common.entities.interfaces.IFireworksProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemFirework.class)
public class MixinItemFirework {
    
    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onItemUse$spawnEntity(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float z, float y_, float x,
                                      CallbackInfoReturnable<EnumActionResult> callback, ItemStack stack, EntityFireworkRocket rocket) {
        ((IFireworksProjectile)rocket).setOwner(player);
    }
    
}
