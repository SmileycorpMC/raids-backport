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
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
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
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(IS_CHARGING_CROSSBOW, false);
    }
    
    @Override
    public boolean isChargingCrossbow() {
        return dataManager.get(IS_CHARGING_CROSSBOW);
    }
    
    @Override
    public void setChargingCrossbow(boolean charging) {
        dataManager.set(IS_CHARGING_CROSSBOW, charging);
    }
    
    @Override
    public IllagerArmPose getArmPose() {
        return null;
    }
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        setEquipmentBasedOnDifficulty(difficulty);
        setEnchantmentBasedOnDifficulty(difficulty);
        return super.onInitialSpawn(difficulty, livingdata);
    }
    
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(RaidsContent.CROSSBOW));
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
	public void setSwingingArms(boolean swingingArms) {
        this.setAggressive(1, swingingArms);
    }
	
	@Override
	protected ResourceLocation getLootTable() {
        return RaidsContent.PILLAGER_DROPS;
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
