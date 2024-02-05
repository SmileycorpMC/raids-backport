package net.smileycorp.raids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.smileycorp.raids.common.entities.IFireworksDamage;
import net.smileycorp.raids.common.entities.IFireworksProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(EntityFireworkRocket.class)
public abstract class MixinEntityFireworkRocket extends Entity implements IFireworksProjectile {
    
    @Shadow protected abstract void dealExplosionDamage();
    
    @Shadow public abstract boolean isAttachedToEntity();
    
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
        x = x + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        motionX = x;
        motionY = y;
        motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        rotationPitch = (float)(MathHelper.atan2(y, f1) * (180D / Math.PI));
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @Inject(method = "entityInit", at = @At("HEAD"), cancellable = true)
    public void entityInit(CallbackInfo ci) {
        dataManager.register(SHOT_AT_ANGLE, false);
    }

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"), cancellable = true)
    public void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (ownerUUID != null) compound.setUniqueId("Owner", ownerUUID);
        compound.setBoolean("ShotAtAngle", dataManager.get(SHOT_AT_ANGLE));
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"), cancellable = true)
    public void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("Owner")) ownerUUID = compound.getUniqueId("Owner");
        if (compound.hasKey("ShotAtAngle")) dataManager.set(SHOT_AT_ANGLE, compound.getBoolean("ShotAtAngle"));
    }
    
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityFireworkRocket;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void onUpdate$move(CallbackInfo ci) {
        if (isShotAtAngle()) {
            motionX /= 1.15;
            motionZ /= 1.15;
            motionY -= 0.04;
        }
    }
    
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sqrt(D)F"))
    public void onUpdate(CallbackInfo callback) {
        if (isAttachedToEntity()) return;
        Vec3d vec3d1 = new Vec3d(posX, posY, posZ);
        Vec3d vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
        vec3d1 = new Vec3d(posX, posY, posZ);
        vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        if (raytraceresult != null) {
            vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }
        Entity entity = findEntityOnPath(vec3d1, vec3d);
        if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
        }
        if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;
            if (getOwner() instanceof EntityPlayer && !((EntityPlayer)getOwner()).canAttackPlayer(entityplayer)) {
                raytraceresult = null;
            }
        }
        if (raytraceresult != null && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY || raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                world.setEntityState(this, (byte)17);
                dealExplosionDamage();
                setDead();
            }
        }
    }
    
    @ModifyArg(method = "dealExplosionDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
    public DamageSource dealExplosionDamage$AttackEntityFrom(DamageSource source) {
        if (source == DamageSource.FIREWORKS && hasOwner()) ((IFireworksDamage)source).setFireworksEntity((EntityFireworkRocket)(Object)this);
        return source;
    }
    
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity entity = null;
        List<Entity> list = world.getEntitiesInAABBexcluding(this,
                getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D), EntityArrow.ARROW_TARGETS);
        double d0 = 0.0D;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = list.get(i);
            if (entity1 != getOwner()) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
                if (raytraceresult != null) {
                    double d1 = start.squareDistanceTo(raytraceresult.hitVec);
                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

}
