package net.smileycorp.raids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.smileycorp.raids.common.entities.ICrossbowArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityArrow.class)
public abstract class MixinEntityArrow extends Entity implements ICrossbowArrow {

    private boolean shotFromCrossbow = false;
    private SoundEvent sound = SoundEvents.ENTITY_ARROW_HIT;
    private byte pierceLevel  = 0;

    public MixinEntityArrow(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setShotFromCrossbow(boolean crossbow) {
        shotFromCrossbow = true;
    }

    @Override
    public void setSoundEvent(SoundEvent sound) {
        this.sound = sound;
    }

    @Override
    public void setPierceLevel(byte level) {
        pierceLevel = level;
    }

    @Override
    public boolean shotFromCrossbow() {
        return shotFromCrossbow;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return sound;
    }

    @Override
    public byte getPierceLevel() {
        return pierceLevel;
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityArrow;playSound(Lnet/minecraft/util/SoundEvent;FF)V"))
    public void playSound (EntityArrow instance, SoundEvent soundEvent, float volume, float pitch) {
        playSound(getSoundEvent(), volume, pitch);
    }

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"), cancellable = true)
    public void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        compound.setByte("PierceLevel", getPierceLevel());
        compound.setString("SoundEvent", sound.getSoundName().toString());
        compound.setBoolean("ShotFromCrossbow", shotFromCrossbow());
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"), cancellable = true)
    public void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        setPierceLevel(compound.getByte("PierceLevel"));
        sound = new SoundEvent(new ResourceLocation(compound.getString("SoundEvent")));
        setShotFromCrossbow(compound.getBoolean("ShotFromCrossbow"));
    }

}
