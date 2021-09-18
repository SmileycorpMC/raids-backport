package net.smileycorp.raids.common.entities;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsContent;

public class EntityRavager extends EntityMob {

	public EntityRavager(World world) {
		super(world);
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return RaidsContent.RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RaidsContent.RAVAGER_DEATH;
   	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return RaidsContent.RAVAGER_HURT;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
        return RaidsContent.RAVAGER_DROPS;
    }

}
