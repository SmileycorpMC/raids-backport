package net.smileycorp.raids.common.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.ai.EntityAIAttackRangedCrossbow;

public class EntityPillager extends AbstractIllager implements ICrossbowAttackMob {

    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.createKey(EntityPillager.class, DataSerializers.BOOLEAN);
    private static final float CROSSBOW_POWER = 1.6F;

	public EntityPillager(World world) {
		super(world);
	}

    @Override
    public IllagerArmPose getArmPose() {
        return null;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(IS_CHARGING_CROSSBOW, false);
    }
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(3, new EntityAIAttackRangedCrossbow(this, 1.0D, 20));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 15F, 1F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLivingBase.class, 15F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, AbstractIllager.class));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }
	
	@Override
	protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
    }
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(RaidsContent.CROSSBOW));
        return super.onInitialSpawn(difficulty, livingdata);
    }

	@Override
	 public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        EntityArrow entityarrow = this.getArrow(distance);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow)
            entityarrow = ((net.minecraft.item.ItemBow) this.getHeldItemMainhand().getItem()).customizeArrow(entityarrow);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - this.world.getDifficulty().getDifficultyId() * 4);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }

    protected EntityArrow getArrow(float p_190726_1_) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        entitytippedarrow.setEnchantmentEffectsFromEntity(this, p_190726_1_);
        return entitytippedarrow;
    }

	@Override
	public void setSwingingArms(boolean swingingArms) {
        this.setAggressive(1, swingingArms);
    }
	
	@Override
	protected SoundEvent getAmbientSound() {
		return RaidsContent.PILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RaidsContent.PILLAGER_DEATH;
   	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return RaidsContent.PILLAGER_HURT;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
        return RaidsContent.PILLAGER_DROPS;
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
        dataManager.set(IS_CHARGING_CROSSBOW, charging);
    }
    
    @Override
    public boolean isChargingCrossbow() {
        return dataManager.get(IS_CHARGING_CROSSBOW);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        idleTime = 0;
    }
    @Override
    public EntityLivingBase getTarget() {
        return getAttackTarget();
    }

}
