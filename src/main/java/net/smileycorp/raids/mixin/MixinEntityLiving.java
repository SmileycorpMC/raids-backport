package net.smileycorp.raids.mixin;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.smileycorp.raids.common.raid.RaidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase {

    public MixinEntityLiving(World world) {
        super(world);
    }

    @Inject(at = @At("HEAD"), method = "setAttackTarget", cancellable = true)
    public void raids$setAttackTarget(EntityLivingBase entity, CallbackInfo callback) {
        if (!RaidHandler.hasActiveRaid(this) |! RaidHandler.hasActiveRaid(entity)) return;
        callback.cancel();
    }
    
}
