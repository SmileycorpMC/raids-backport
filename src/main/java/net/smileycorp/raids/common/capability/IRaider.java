package net.smileycorp.raids.common.capability;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.raids.common.RaidsContent;

public interface IRaider {
	
	public IRaid getRaid();
	
	public boolean hasRaid();
	
	public void setRaid(IRaid raid);
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound);

	public void readNBT(NBTTagCompound nbt);
	
	public boolean isRaidActive();
	
	public boolean isLeader();
	
	public void setLeader();
	
	public class Storage implements IStorage<IRaider> {

		@Override
		public NBTBase writeNBT(Capability<IRaider> capability, IRaider instance, EnumFacing side) {
			return instance.writeNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<IRaider> capability, IRaider instance, EnumFacing side, NBTBase nbt) {
			instance.readNBT((NBTTagCompound) nbt);
		}
		
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		
		protected IRaider impl;
		
		public Provider(EntityLiving entity) {
			impl = new Raider(entity);
		}

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == RaidsContent.RAIDER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? RaidsContent.RAIDER_CAPABILITY.cast(impl) : null;
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
