package net.smileycorp.raids.common.capability;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.village.Village;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.atlas.api.IOngoingEvent;
import net.smileycorp.raids.common.RaidsContent;

public interface IRaid extends IOngoingEvent {

	public Village getVillage();
	
	public float getWaveHealth();
	
	public float getTotalWaveHealth();
	
	public List<EntityLiving> getWaveEntities();
	
	public void startEvent(int maxWaves, int bonusWaves, int level);
	
	public void startNextWave();
	
	public void endEvent();
	
	public void takeDamage(EntityLiving entity, DamageSource source, float damage);
	
	public void entityDie(EntityLiving entity);
	
	public int entitiesRemaining();
	
	public class Storage implements IStorage<IRaid> {

		@Override
		public NBTBase writeNBT(Capability<IRaid> capability, IRaid instance, EnumFacing side) {
			return instance.writeToNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<IRaid> capability, IRaid instance, EnumFacing side, NBTBase nbt) {
			instance.readFromNBT((NBTTagCompound) nbt);
		}
		
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		
		protected IRaid impl;
		
		public Provider(Village village) {
			impl = new Raid(village);
		}

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == RaidsContent.RAID_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? RaidsContent.RAID_CAPABILITY.cast(impl) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return impl.writeToNBT(new NBTTagCompound());
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			impl.readFromNBT(nbt);
		}
		
	}
}
