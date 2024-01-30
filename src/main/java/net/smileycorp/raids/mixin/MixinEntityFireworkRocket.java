package net.smileycorp.raids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.entities.IFireworksProjectile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityFireworkRocket.class)
public abstract class MixinEntityFireworkRocket extends Entity implements IFireworksProjectile {

    @Shadow @Final private static DataParameter<ItemStack> FIREWORK_ITEM;
    @Shadow private int lifetime;
    private static final DataParameter<Boolean> SHOT_AT_ANGLE = EntityDataManager.createKey(EntityFireworkRocket.class, DataSerializers.BOOLEAN);

    private UUID ownerUUID;
    private Entity cachedOwner;

    public MixinEntityFireworkRocket(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setOwner(Entity owner) {
        if (owner != null) {
            ownerUUID = owner.getUniqueID();
            cachedOwner = owner;
        }
    }

    @Override
    public Entity getOwner() {
        if (cachedOwner != null && !cachedOwner.isAddedToWorld()) {
            return cachedOwner;
        } else if (ownerUUID != null && world instanceof WorldServer) {
            cachedOwner = ((WorldServer)world).getEntityFromUuid(ownerUUID);
            return cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasOwner() {
        return ownerUUID != null;
    }

    @Override
    public boolean isShotAtAngle() {
        return dataManager.get(SHOT_AT_ANGLE);
    }

    @Override
    public void setShotAtAngle() {
        dataManager.set(SHOT_AT_ANGLE, true);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Inject(method = "entityInit", at = @At("HEAD"), cancellable = true)
    public void entityInit(CallbackInfo ci) {
        dataManager.register(SHOT_AT_ANGLE, false);
    }

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"), cancellable = true)
    public void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (ownerUUID != null) compound.setUniqueId("Owner", this.ownerUUID);
        compound.setBoolean("ShotAtAngle", dataManager.get(SHOT_AT_ANGLE));
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"), cancellable = true)
    public void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("Owner")) ownerUUID = compound.getUniqueId("Owner");
        if (compound.hasKey("ShotAtAngle")) dataManager.set(SHOT_AT_ANGLE, compound.getBoolean("ShotAtAngle"));
    }

}
