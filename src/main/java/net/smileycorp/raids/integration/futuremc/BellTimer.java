package net.smileycorp.raids.integration.futuremc;

import com.google.common.collect.Lists;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.ai.EntityAILongDistancePatrol;
import net.smileycorp.raids.common.entities.ai.EntityAIPathfindToRaid;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.raid.WorldDataRaids;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.tektopia.TektopiaIntegration;

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
