package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.ai.EntityAILongDistancePatrol;
import net.smileycorp.raids.common.entities.ai.EntityAIPathfindToRaid;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.tektopia.TektopiaIntegration;

public interface Raider {
	
	Raid getCurrentRaid();
	
	boolean hasActiveRaid();
	
	void setCurrentRaid(Raid raid);
	
	NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound);

	void readNBT(NBTTagCompound nbt);
	
	boolean isLeader();
	
	void setLeader();
	
	int getTicksOutsideRaid();
	
	void setTicksOutsideRaid(int ticks);
	
	int getWave();
	
	void setWave(int ticks);
	
	boolean isPatrolLeader();
	
	void setPatrolLeader(boolean patrolLeader);
	
	void findPatrolTarget();
	
	void setPatrolTarget(BlockPos blockpos);
	
	BlockPos getPatrolTarget();
	
	boolean isPatrolling();
	
	boolean canBeCaptain();
	
	float getCaptainChance();
	
	class Impl implements Raider {

		protected final EntityLiving entity;
		protected Raid raid = null;
		protected boolean patrolLeader;
		private int ticksOutsideRaid;
		private int wave;
		private BlockPos patrolTarget;
		private boolean hasAI = false;
		
		public Impl() {
			entity = null;
		}

		public Impl(EntityLiving entity) {
			this.entity = entity;
		}

		@Override
		public Raid getCurrentRaid() {
			return raid;
		}

		@Override
		public boolean hasActiveRaid() {
			return raid != null;
		}

		@Override
		public void setCurrentRaid(Raid raid) {
			this.raid=raid;
		}

		@Override
		public NBTTagCompound writeNBT(NBTTagCompound nbt) {
			nbt.setBoolean("PatrolLeader", patrolLeader);
			nbt.setInteger("Wave", wave);
			if (raid != null) nbt.setInteger("RaidId", raid.getId());
			if (patrolTarget != null) nbt.setTag("PatrolTarget", NBTUtil.createPosTag(patrolTarget));
			return nbt;
		}

		@Override
		public void readNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("PatrolLeader")) patrolLeader = nbt.getBoolean("PatrolLeader");
			if (nbt.hasKey("Wave")) wave = nbt.getInteger("Wave");
			if (nbt.hasKey("RaidId") && entity != null && entity.world instanceof WorldServer)
				raid = WorldDataRaids.getData((WorldServer) entity.world).get(nbt.getInteger("RaidId"));
			if (raid != null) {
				raid.addWaveMob(wave, entity, false);
				if (entity instanceof EntityCreature) {
					entity.tasks.addTask(3, new EntityAIPathfindToRaid(this, (EntityCreature) entity));
					entity.tasks.addTask(4, new EntityAIMoveThroughVillage((EntityCreature) entity, 1, true));
					entity.targetTasks.addTask(3, new EntityAINearestAttackableTarget((EntityCreature) entity, EntityVillager.class, true));
					entity.targetTasks.addTask(3, new EntityAINearestAttackableTarget((EntityCreature) entity, EntityIronGolem.class, true));
					if (ModIntegration.TEKTOPIA_LOADED) TektopiaIntegration.addTargetTask((EntityCreature) entity);
				}
			}
			if (nbt.hasKey("PatrolTarget")) setPatrolTarget(NBTUtil.getPosFromTag(nbt.getCompoundTag("PatrolTarget")));
		}

		@Override
		public boolean isLeader() {
			return patrolLeader;
		}

		@Override
		public void setLeader() {
			if (entity != null) {
				if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
					entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, Constants.ominousBanner());
					entity.setDropChance(EntityEquipmentSlot.HEAD, 2.0F);
				}
			}
			patrolLeader = true;
		}
		
		@Override
		public int getTicksOutsideRaid() {
			return ticksOutsideRaid;
		}
		
		@Override
		public void setTicksOutsideRaid(int ticks) {
			ticksOutsideRaid = ticks;
		}
		
		@Override
		public int getWave() {
			return wave;
		}
		
		@Override
		public void setWave(int ticks) {
			this.wave = wave;
		}
		
		@Override
		public boolean isPatrolLeader() {
			return patrolLeader;
		}
		
		@Override
		public void setPatrolLeader(boolean patrolLeader) {
			this.patrolLeader = patrolLeader;
		}
		
		@Override
		public void findPatrolTarget() {
			if (entity == null) return;
			setPatrolTarget(entity.getPosition().add(-500 + entity.getRNG().nextInt(1000), 0, -500 + entity.getRNG().nextInt(1000)));
			
		}
		
		@Override
		public void setPatrolTarget(BlockPos patrolTarget) {
			this.patrolTarget = patrolTarget;
			if (!hasAI && entity != null) {
				entity.tasks.addTask(4, new EntityAILongDistancePatrol(this, entity));
				hasAI = true;
			}
		}
		
		@Override
		public BlockPos getPatrolTarget() {
			return patrolTarget;
		}
		
		@Override
		public boolean isPatrolling() {
			return patrolTarget != null && !hasActiveRaid();
		}
		
		@Override
		public boolean canBeCaptain() {
			return RaidHandler.canBeCaptain(entity);
		}
		
		@Override
		public float getCaptainChance() {
			return EntityConfig.getCaptainChance(entity);
		}
		
	}
	
	class Storage implements IStorage<Raider> {

		@Override
		public NBTBase writeNBT(Capability<Raider> capability, Raider instance, EnumFacing side) {
			return instance.writeNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<Raider> capability, Raider instance, EnumFacing side, NBTBase nbt) {
			instance.readNBT((NBTTagCompound) nbt);
		}
		
	}
	
	class Provider implements ICapabilitySerializable<NBTTagCompound> {
		
		protected Raider impl;
		
		public Provider(EntityLiving entity) {
			impl = new Impl(entity);
		}

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == RaidsContent.RAIDER;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? RaidsContent.RAIDER.cast(impl) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return impl.writeNBT(new NBTTagCompound());
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			impl.readNBT(nbt);
		}
		
	}
	
}
