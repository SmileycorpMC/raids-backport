package net.smileycorp.raids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.smileycorp.raids.common.entities.IFireworksDamage;
import net.smileycorp.raids.common.entities.IFireworksProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class MixinDamageSource implements IFireworksDamage {
    
    private EntityFireworkRocket rocket;
    
    @Override
    public void setFireworksEntity(EntityFireworkRocket projectile) {
        rocket = projectile;
    }
    
    @Override
    public boolean hasFireworksEntity() {
        return rocket != null;
    }
    
    @Override
    public EntityFireworkRocket getFireworksEntity() {
        return rocket;
    }
    
    @Inject(method = "getImmediateSource", at = @At("HEAD"), cancellable = true)
    public void getImmediateSource(CallbackInfoReturnable<Entity> callback) {
        if ((Object)this == DamageSource.FIREWORKS && hasFireworksEntity()) callback.setReturnValue(getFireworksEntity());
    }
    
    @Inject(method = "getTrueSource", at = @At("HEAD"), cancellable = true)
    public void getTrueSource(CallbackInfoReturnable<Entity> callback) {
        if ((Object)this == DamageSource.FIREWORKS && hasFireworksEntity()) callback.setReturnValue(((IFireworksProjectile)getFireworksEntity()).getOwner());
    }
    
    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    public void getDeathMessage(EntityLivingBase entity, CallbackInfoReturnable<ITextComponent> callback) {
        if ((Object)this == DamageSource.FIREWORKS && hasFireworksEntity() && ((IFireworksProjectile)getFireworksEntity()).getOwner() != entity) callback.setReturnValue(
                new TextComponentTranslation("death.attack.raids.fireworks.player", entity, ((IFireworksProjectile)getFireworksEntity()).getOwner()));
    }
    
}
