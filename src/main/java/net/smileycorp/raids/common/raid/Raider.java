package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.raids.common.RaidsContent;

public interface Raider {
	
	Raid getCurrentRaid();
	
	boolean hasActiveRaid();
	
	void setCurrentRaid(Raid raid);
	
	NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound);

	void readNBT(NBTTagCompound nbt);
	
	boolean isRaidActive();
	
	boolean isLeader();
	
	void setLeader();
	
	int getTicksOutsideRaid();
	
	void setTicksOutsideRaid(int ticks);
	
	int getWave();
	
	void setWave(int ticks);
	
	boolean isPatrolLeader();
	
	void setPatrolLeader(boolean patrolLeader);

	class Impl implements Raider {

		protected final EntityLiving entity;
		protected Raid raid = null;
		protected boolean patrolLeader;
		private int ticksOutsideRaid;
		private int wave;

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
			return nbt;
		}

		@Override
		public void readNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("PatrolLeader")) patrolLeader = nbt.getBoolean("PatrolLeader");
			if (nbt.hasKey("Wave")) wave = nbt.getInteger("Wave");
			if (nbt.hasKey("RaidId") && entity != null && entity.world instanceof WorldServer)
				raid = WorldDataRaids.getData((WorldServer) entity.world).get(nbt.getInteger("RaidId"));
			if (raid != null) raid.addWaveMob(wave, entity, false);
		}

		@Override
		public boolean isRaidActive() {
			if (hasActiveRaid() && entity != null) return raid.isActive();
			return false;
		}

		@Override
		public boolean isLeader() {
			return patrolLeader;
		}

		@Override
		public void setLeader() {
			if (entity != null) {
				if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
					entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, RaidsContent.createOminousBanner());
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
