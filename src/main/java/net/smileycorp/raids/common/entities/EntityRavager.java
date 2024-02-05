package net.smileycorp.raids.common.entities;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.MathUtils;
import net.smileycorp.raids.common.RaidHandler;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ai.EntityAIRavagerAttackMelee;

import javax.annotation.Nullable;
import java.util.List;

public class EntityRavager extends EntityMob {
	
	private static final Predicate<EntityLiving> NO_RAVAGER_AND_ALIVE = entity -> entity.isEntityAlive() && !(entity instanceof EntityRavager);
	
	private int attackTick;
	private int stunnedTick;
	private int roarTick;

	public EntityRavager(World world) {
		super(world);
		setSize(1.95F, 2.2F);
		stepHeight = 1f;
		experienceValue = 20;
	}
	
	@Override
	public void setAttackTarget(@Nullable EntityLivingBase target) {
		if (RaidHandler.CAPABILITY_ENTITIES.contains(target)) return;
		super.setAttackTarget(target);
	}
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(4, new EntityAIRavagerAttackMelee(this));
		tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 15F, 1F));
		tasks.addTask(10, new EntityAIWatchClosest(this, EntityLivingBase.class, 15F));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, AbstractIllager.class));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("AttackTick", attackTick);
		nbt.setInteger("StunTick", stunnedTick);
		nbt.setInteger("RoarTick", roarTick);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		attackTick = nbt.getInteger("AttackTick");
		stunnedTick = nbt.getInteger("StunTick");
		roarTick = nbt.getInteger("RoarTick");
	}
	
	@Override
	public Entity getControllingPassenger() {
		if (!isAIDisabled()) {
			List<Entity> list = getPassengers();
			if (!list.isEmpty()) return list.get(0);
		}
		return null;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isEntityAlive()) {
			if (isImmobile()) {
				getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			} else {
				double d0 = getAttackTarget() != null ? 0.35D : 0.3D;
				IAttributeInstance attribute = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				double d1 = attribute.getBaseValue();
				attribute.setBaseValue(MathUtils.lerp(0.1D, d1, d0));
			}
			if (collidedHorizontally && ForgeEventFactory.getMobGriefingEvent(world, this)) {
				boolean flag = false;
				AxisAlignedBB aabb = getEntityBoundingBox().grow(0.2D);
				BlockPos pos = new BlockPos(aabb.minX, aabb.minY, aabb.minZ);
				for (int i = 0; i < Math.floor(aabb.minX) - Math.floor(aabb.maxX); i++) {
					for (int j = 0; j < Math.floor(aabb.minY) - Math.floor(aabb.maxY); j++) {
						for (int k = 0; k < Math.floor(aabb.minZ) - Math.floor(aabb.maxZ); k++) {
							BlockPos blockpos = pos.add(i, j, k);
							IBlockState blockstate = world.getBlockState(blockpos);
							Block block = blockstate.getBlock();
							if (block instanceof BlockLeaves) {
								flag = world.destroyBlock(blockpos, true) || flag;
							}
						}
					}
				}
				if (!flag && onGround) jump();
			}
			if (roarTick > 0) {
				roarTick--;
				if (roarTick == 10) roar();
			}
			if (attackTick > 0) attackTick--;
			if (stunnedTick > 0) {
				--stunnedTick;
				stunEffect();
				if (stunnedTick == 0) {
					playSound(RaidsSoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
					roarTick = 20;
				}
			}
			
		}
	}
	
	private void stunEffect() {
		if (rand.nextInt(6) == 0) {
			double d0 = posX - (double)width * Math.sin(rotationYaw * ((float)Math.PI / 180F)) + (rand.nextDouble() * 0.6D - 0.3D);
			double d1 = posY + (double)height - 0.3D;
			double d2 = posZ + (double)width * Math.cos(rotationYaw * ((float)Math.PI / 180F)) + (rand.nextDouble() * 0.6D - 0.3D);
			world.spawnParticle(EnumParticleTypes.SPELL_MOB, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
		}
	}
	
	protected void blockUsingShield(EntityLivingBase entity) {
		if (roarTick == 0) {
			if (rand.nextDouble() < 0.5D) {
				stunnedTick = 40;
				playSound(RaidsSoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
				//world.(this, (byte)39);
				entity.applyEntityCollision(this);
			} else strongKnockback(entity);
			entity.velocityChanged = true;
		}
	}
	
	protected boolean isImmobile() {
		return !isEntityAlive() || attackTick > 0 || stunnedTick > 0 || roarTick > 0;
	}
	
	@Override
	public boolean canEntityBeSeen(Entity entity) {
		return stunnedTick <= 0 && roarTick <= 0 ? super.canEntityBeSeen(entity) : false;
	}
	
	private void roar() {
		if (!isEntityAlive()) return;
		for(EntityLiving livingentity : world.getEntitiesWithinAABB(EntityLiving.class, getEntityBoundingBox().grow(4.0D), NO_RAVAGER_AND_ALIVE)) {
			if (!(livingentity instanceof AbstractIllager)) {
				livingentity.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F);
			}
			strongKnockback(livingentity);
		}
		Vec3d vec3 = getEntityBoundingBox().getCenter();
		for(int i = 0; i < 40; ++i) {
			double d0 = rand.nextGaussian() * 0.2D;
			double d1 = rand.nextGaussian() * 0.2D;
			double d2 = rand.nextGaussian() * 0.2D;
			world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, vec3.x, vec3.y, vec3.z, d0, d1, d2);
		}
		//gameEvent(GameEvent.ENTITY_ROAR);
	}
	
	private void strongKnockback(Entity entity) {
		double d0 = entity.posX - posX;
		double d1 = entity.posZ - posZ;
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
		entity.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
	}
	
	public int getAttackTick() {
		return attackTick;
	}
	
	public int getStunnedTick() {
		return stunnedTick;
	}
	
	public int getRoarTick() {
		return roarTick;
	}
	
	public boolean attackEntityAsMob(Entity entity) {
		attackTick = 10;
		//world.broadcast(EntityEvent(this, (byte)4);
		playSound(RaidsSoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
		return super.attackEntityAsMob(entity);
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return RaidsSoundEvents.RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RaidsSoundEvents.RAVAGER_DEATH;
   	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return RaidsSoundEvents.RAVAGER_HURT;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		playSound(RaidsSoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
        return Constants.RAVAGER_DROPS;
    }

}
