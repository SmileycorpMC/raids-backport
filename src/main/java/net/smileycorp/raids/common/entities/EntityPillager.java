package net.smileycorp.raids.common.entities;

import git.jbredwards.crossbow.api.ICrossbow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsAdvancements;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ai.EntityAIPillagerCrossbowAttack;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.config.EntityConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;

import javax.vecmath.Vector3f;

@Optional.Interface(iface = "git.jbredwards.crossbow.api.ICrossbowUser", modid = "crossbow")
public class EntityPillager extends AbstractIllager implements IRangedAttackMob {

    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.createKey(EntityPillager.class, DataSerializers.BOOLEAN);

	public EntityPillager(World world) {
		super(world);
	}
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		tasks.addTask(0, new EntityAISwimming(this));
        if (ModIntegration.HAS_CROSSBOW_MOD) tasks.addTask(3, new EntityAIPillagerCrossbowAttack(this));
        else tasks.addTask(4, new EntityAIAttackRangedBow(this, 1, 20, 15));
        tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1));
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 15, 1));
        tasks.addTask(10, new EntityAIWatchClosest(this, EntityLivingBase.class, 15));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, AbstractIllager.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }
    
    @Override
    protected boolean isValidLightLevel() {
        return world.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(posX, getEntityBoundingBox().minY, posZ)) <= 8;
    }
    
    @Override
    public void setAttackTarget(EntityLivingBase target) {
        if (RaidHandler.isRaider(target)) return;
        super.setAttackTarget(target);
    }
	
	@Override
	protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityConfig.pillager.applyAttributes(this);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IS_CHARGING_CROSSBOW, false);
    }
    
    public boolean isChargingCrossbow() {
        return dataManager.get(IS_CHARGING_CROSSBOW);
    }
    
    public void setChargingCrossbow(boolean charging) {
        dataManager.set(IS_CHARGING_CROSSBOW, charging);
    }
    
    @Override
    public IllagerArmPose getArmPose() {
        if (!ModIntegration.HAS_CROSSBOW_MOD) return getHeldItemMainhand().isEmpty() && getHeldItemOffhand().isEmpty()
                ? IllagerArmPose.ATTACKING : IllagerArmPose.BOW_AND_ARROW;
        if (ModIntegration.isCrossbow(getHeldItemMainhand()) || ModIntegration.isCrossbow(getHeldItemOffhand())) return null;
        if (!getHeldItemMainhand().isEmpty()) return IllagerArmPose.BOW_AND_ARROW;
        return getHeldItemOffhand().isEmpty() ? IllagerArmPose.ATTACKING : IllagerArmPose.BOW_AND_ARROW;
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
        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ModIntegration.getPillagerItem(rand));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return RaidsSoundEvents.PILLAGER_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return RaidsSoundEvents.PILLAGER_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return RaidsSoundEvents.PILLAGER_HURT;
    }
    
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        EntityArrow entityarrow = getArrow(distance);
        if (getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow)
            entityarrow = ((net.minecraft.item.ItemBow) this.getHeldItemMainhand().getItem()).customizeArrow(entityarrow);
        double d0 = target.posX - posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3f) - entityarrow.posY;
        double d2 = target.posZ - posZ;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6f, (float)(14 - world.getDifficulty().getDifficultyId() * 4));
        playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1, 1f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        world.spawnEntity(entityarrow);
    }
    
    protected EntityArrow getArrow(float distance) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, this);
        entitytippedarrow.setEnchantmentEffectsFromEntity(this, distance);
        return entitytippedarrow;
    }
    
    @Override
	public void setSwingingArms(boolean swingingArms) {
        setAggressive(1, swingingArms);
    }
	
	@Override
	protected ResourceLocation getLootTable() {
        return Constants.PILLAGER_DROPS;
    }
    
    public void onCrossbowAttackPerformed() {
        idleTime = 0;
    }
    
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!ModIntegration.CROSSBOWS_BACKPORT_LOADED) return;
        if (CrossbowsBackportIntegration.isCrossbowProjectile(source.getImmediateSource()) && source.getTrueSource() instanceof EntityPlayerMP)
            RaidsAdvancements.WHOS_THE_PILLAGER.trigger((EntityPlayerMP) source.getTrueSource());
    }
    
    @Optional.Method(modid = "crossbow")
    public ItemStack findAmmo(ItemStack stack) {
        return new ItemStack(Items.ARROW);
    }
    
    @Optional.Method(modid = "crossbow")
    public void setCharging(boolean charging) {
        setChargingCrossbow(charging);
    }
    
    @Optional.Method(modid = "crossbow")
    public void shootAtTarget(ItemStack crossbow, IProjectile projectile, double multishotOffset) {
        EntityLivingBase target = getAttackTarget();
        double d0 = target.posX - posX;
        double d1 = target.posZ - posZ;
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = target.posY - posY + d2 * (double)0.2F;
        Vec3d vec3 = new Vec3d(d0, d3, d1).normalize();
        Vec3d vec31 = vec3.crossProduct(new Vec3d(0.0D, 1, 0.0D));
        Vector3f angle = new Vector3f((float) vec31.x, (float) vec31.y, (float) vec31.z);
        Vector3f vector3f = new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
        vector3f.angle(angle);
        projectile.shoot(vector3f.x, vector3f.y, vector3f.z, 1.6f, (float)(14 - world.getDifficulty().getDifficultyId() * 4));
        playSound(((ICrossbow)crossbow.getItem()).getShootSound(this, crossbow, projectile, multishotOffset),
                1, 1F / (getRNG().nextFloat() * 0.4F + 0.8F));
    }
    
}
