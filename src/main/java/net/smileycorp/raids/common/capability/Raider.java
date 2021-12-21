package net.smileycorp.raids.common.capability;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.smileycorp.raids.common.RaidsContent;

public class Raider implements IRaider {
	
	protected final EntityLiving entity;
	protected IRaid raid = null;
	protected boolean raidLeader;
	
	public Raider() {
		entity = null;
	}

	public Raider(EntityLiving entity) {
		this.entity=entity;
	}

	@Override
	public IRaid getRaid() {
		return raid;
	}

	@Override
	public boolean hasRaid() {
		return raid != null;
	}

	@Override
	public void setRaid(IRaid raid) {
		this.raid=raid;
	}

	@Override
	public NBTTagCompound writeNBT(NBTTagCompound nbt) {
		nbt.setBoolean("raidLeader", raidLeader);
		BlockPos pos = raid.getVillage().getCenter();
		NBTTagCompound villagePos = new NBTTagCompound();
		villagePos.setInteger("x", pos.getX());
		villagePos.setInteger("y", pos.getY());
		villagePos.setInteger("z", pos.getZ());
		nbt.setTag("villagePos", villagePos);
		return nbt;
	}

	@Override
	public void readNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("raidLeader"))raidLeader = nbt.getBoolean("raidLeader");
		if (nbt.hasKey("villagePos") && entity!=null) {
			NBTTagCompound villagePos = nbt.getCompoundTag("pos");
			Village village = entity.world.villageCollection.getNearestVillage(
					new BlockPos(villagePos.getInteger("x"), villagePos.getInteger("y"), villagePos.getInteger("z")), 0);
			if (village!=null) {
				if (village.hasCapability(RaidsContent.RAID_CAPABILITY, null)) raid = village.getCapability(RaidsContent.RAID_CAPABILITY, null);
			}
		}
	}

	@Override
	public boolean isRaidActive() {
		if (hasRaid() && entity != null) return raid.isActive(entity.world);
		return false;
	}

	@Override
	public boolean isLeader() {
		return raidLeader;
	}

	@Override
	public void setLeader() {
		if (entity!=null) {
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
				entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, RaidsContent.OMINOUS_BANNER);
			}
		}
		raidLeader = true;
	}
	
}