package net.smileycorp.raids.integration.futuremc;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.util.List;

public interface BellTimer {
	
	List<BellTimer> ACTIVE_BELLS = Lists.newArrayList();
	
	boolean isRinging();
	
	void updateTimer();
	
	void setRinging();
	
	class Impl implements BellTimer {
		
		private final TileEntity tile;
		
		private int timer = 0;
		
		public Impl(TileEntity tile) {
			this.tile = tile;
		}
		
		@Override
		public boolean isRinging() {
			return timer > 0;
		}
		
		@Override
		public void updateTimer() {
			if (timer-- > 0) return;
			if (timer == 40) {
				if (tile != null &! tile.getWorld().getEntitiesWithinAABB(EntityLiving.class,
						new AxisAlignedBB(tile.getPos()).grow(48.0D), RaidHandler::hasActiveRaid).isEmpty()) {
					timer = 0;
					ACTIVE_BELLS.remove(this);
				}
			}
			if (tile != null) RaidHandler.findRaiders(tile.getWorld(), tile.getPos());
			ACTIVE_BELLS.remove(this);
		}
		
		@Override
		public void setRinging() {
			timer = 45;
			ACTIVE_BELLS.add(this);
		}
		
	}
	
	class Storage implements IStorage<BellTimer> {

		@Override
		public NBTBase writeNBT(Capability<BellTimer> capability, BellTimer instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT(Capability<BellTimer> capability, BellTimer instance, EnumFacing side, NBTBase nbt) {}
		
	}
	
	class Provider implements ICapabilityProvider {
		
		protected BellTimer impl;
		
		public Provider(TileEntity tile) {
			impl = new Impl(tile);
		}

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return cap == FutureMCIntegration.BELL_TIMER;
		}

		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			return hasCapability(cap, facing) ? FutureMCIntegration.BELL_TIMER.cast(impl) : null;
		}
		
	}
	
}
