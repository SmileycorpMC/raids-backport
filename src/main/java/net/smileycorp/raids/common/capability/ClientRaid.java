package net.smileycorp.raids.common.capability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class ClientRaid implements IRaid {

	protected final List<EntityLiving> entities = new ArrayList<EntityLiving>();
	protected float health = 0, totalHealth = 0;
	protected boolean isActive;
	protected Village village = null;

	public ClientRaid() {}

	public ClientRaid(Village village) {
		this.village = village;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return nbt;
	}

	@Override
	public void update(World world) {}

	@Override
	public boolean isActive(World world) {
		return !village.world.isRemote && isActive ;
	}

	@Override
	public float getWaveHealth() {
		return health;
	}

	@Override
	public float getTotalWaveHealth() {
		return totalHealth;
	}

	@Override
	public List<EntityLiving> getWaveEntities() {
		List<EntityLiving> result = new ArrayList<EntityLiving>();
		for (EntityLiving entity : entities) if (!entity.isDead) result.add(entity);
		return result;
	}

	@Override
	public void startEvent(int maxWaves, int bonusWaves, int level) {}

	@Override
	public void startNextWave() {}

	@Override
	public void endEvent() {}

	@Override
	public void takeDamage(EntityLiving entity, DamageSource source, float damage) {
		health -= damage;
	}

	@Override
	public void entityDie(EntityLiving entity) {
		entities.remove(entity);
	}

	@Override
	public int entitiesRemaining() {
		return entities.size();
	}

	@Override
	public Village getVillage() {
		return village;
	}

}