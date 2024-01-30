package net.smileycorp.raids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.smileycorp.raids.common.entities.ICrossbowArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityArrow.class)
public abstract class MixinEntityArrow extends Entity implements ICrossbowArrow {

    private boolean shotFromCrossbow = false;
    private byte pierceLevel  = 0;

    public MixinEntityArrow(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setShotFromCrossbow(boolean crossbow) {
        shotFromCrossbow = true;
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
    public byte getPierceLevel() {
        return pierceLevel;
    }

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"), cancellable = true)
    public void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        compound.setByte("PierceLevel", getPierceLevel());
        compound.setBoolean("ShotFromCrossbow", shotFromCrossbow());
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"), cancellable = true)
    public void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("PierceLevel")) setPierceLevel(compound.getByte("PierceLevel"));
        if (compound.hasKey("ShotFromCrossbow")) setShotFromCrossbow(compound.getBoolean("ShotFromCrossbow"));
    }

}
