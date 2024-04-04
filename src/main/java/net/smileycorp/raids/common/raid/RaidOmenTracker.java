package net.smileycorp.raids.common.raid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.raids.common.RaidsContent;

public interface RaidOmenTracker {
	
	BlockPos getBlockPos();
	
	NBTTagCompound writeNBT();

	void readNBT(NBTTagCompound nbt);
	
	void setBlockPos(BlockPos pos);
	
	class Impl implements RaidOmenTracker {
		
		private BlockPos pos;
		
		@Override
		public BlockPos getBlockPos() {
			return pos;
		}
		
		@Override
		public NBTTagCompound writeNBT() {
			return pos != null ? NBTUtil.createPosTag(pos) : new NBTTagCompound();
		}
		
		@Override
		public void readNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("X") && nbt.hasKey("Y") && nbt.hasKey("Z")) pos = NBTUtil.getPosFromTag(nbt);
		}
		
		@Override
		public void setBlockPos(BlockPos pos) {
			this.pos = pos;
		}
		
	}
	
	class Storage implements IStorage<RaidOmenTracker> {

		@Override
		public NBTBase writeNBT(Capability<RaidOmenTracker> capability, RaidOmenTracker instance, EnumFacing side) {
			return instance.writeNBT();
		}

		@Override
		public void readNBT(Capability<RaidOmenTracker> capability, RaidOmenTracker instance, EnumFacing side, NBTBase nbt) {
			instance.readNBT((NBTTagCompound) nbt);
		}
		
	}
	
	class Provider implements ICapabilitySerializable<NBTTagCompound> {
		
		protected RaidOmenTracker impl = new Impl();;

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == RaidsContent.RAIDER;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? RaidsContent.RAID_OMEN_TRACKER.cast(impl) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return impl.writeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			impl.readNBT(nbt);
		}
		
	}
	
	static void setRaidStart(EntityPlayer player) {
		if (player.hasCapability(RaidsContent.RAID_OMEN_TRACKER, null))
			player.getCapability(RaidsContent.RAID_OMEN_TRACKER, null).setBlockPos(player.getPosition());
	}
	
	static BlockPos getRaidStart(EntityPlayer player) {
		if (player.hasCapability(RaidsContent.RAID_OMEN_TRACKER, null)) {
			BlockPos pos = player.getCapability(RaidsContent.RAID_OMEN_TRACKER, null).getBlockPos();
			if (pos != null) return pos;
		}
		return player.getPosition();
	}
	
}
