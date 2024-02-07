package net.smileycorp.raids.mixin;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.RegistryDefaulted;
import net.smileycorp.raids.common.entities.interfaces.IFireworksProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Bootstrap.class)
public class MixinBootstrap {
    
    @Redirect(method = "registerDispenserBehaviors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryDefaulted;putObject(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static void registerDispenserBehaviours(RegistryDefaulted<Item, IBehaviorDispenseItem> instance, Object key, Object value) {
        if (key == Items.FIREWORKS) {
            instance.putObject((Item)key, new BehaviorDefaultDispenseItem() {
                public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
                    double d0 = source.getX() + facing.getFrontOffsetX();
                    double d1 = (source.getBlockPos().getY() + facing.getFrontOffsetY());
                    double d2 = source.getZ() + facing.getFrontOffsetZ();
                    EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(source.getWorld(), d0, d1, d2, stack);
                    IFireworksProjectile projectile = (IFireworksProjectile) entityfireworkrocket;
                    projectile.setShotAtAngle();
                    projectile.shoot(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ(), 0.5F, 1.0F);
                    source.getWorld().spawnEntity(entityfireworkrocket);
                    stack.shrink(1);
                    return stack;
                }
                protected void playDispenseSound(IBlockSource source) {
                    source.getWorld().playEvent(1004, source.getBlockPos(), 0);
                }
            });
            return;
        }
        instance.putObject((Item)key, (IBehaviorDispenseItem)value);
    }
    
}
