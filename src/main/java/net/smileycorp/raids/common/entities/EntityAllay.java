package net.smileycorp.raids.common.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ai.AIMoveRandomFlying;
import net.smileycorp.raids.common.entities.ai.FlyingMoveControl;
import net.smileycorp.raids.common.util.MathUtils;
import net.smileycorp.raids.config.EntityConfig;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityAllay extends EntityMob implements IEntityOwnable {
    
    private static final DataParameter<Boolean> IS_DANCING = EntityDataManager.createKey(EntityAllay.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CAN_DUPLICATE = EntityDataManager.createKey(EntityAllay.class, DataSerializers.BOOLEAN);
    
    private final InventoryBasic inventory = new InventoryBasic(getName(), false, 1);
    private BlockPos jukebox;
    private EntityPlayer owner;
    private int duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    
    public EntityAllay(World world) {
        super(world);
        moveHelper = new FlyingMoveControl(this);
        setSize(0.35f, 0.6f);
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(8, new AIMoveRandomFlying(this));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityConfig.allay.applyAttributes(this);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IS_DANCING, false);
        dataManager.register(CAN_DUPLICATE, true);
    }
    
    @Override
    protected void damageEntity(DamageSource src, float amount) {
        if (src.getTrueSource() == owner) return;
        super.damageEntity(src, amount);
    }
    
    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!world.isRemote |! isEntityAlive()) return;
        holdingItemAnimationTicks0 = holdingItemAnimationTicks;
        holdingItemAnimationTicks = MathUtils.clamp(holdingItemAnimationTicks + (hasItemInSlot(EntityEquipmentSlot.MAINHAND) ? 1 : -1), 0, 5);
    }
    
    @Override
    public void updateAITasks() {
        super.updateAITasks();
        if (!isEntityAlive()) return;
        if (ticksExisted % 10 == 0) heal(1);
        if (duplicationCooldown > 0) duplicationCooldown--;
        if (duplicationCooldown == 0 &! canDuplicate()) dataManager.set(CAN_DUPLICATE, true);
    }
    
    @Override
    public boolean hasNoGravity() {
        return true;
    }
    
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack stack1 = getHeldItemMainhand();
        if (isDancing() && isDuplicationItem(stack) && canDuplicate()) {
            stack.shrink(1);
            duplicate();
            player.setActiveHand(hand);
            player.swingArm(hand);
            return true;
        }
        if (!stack.isEmpty() &!(ItemStack.areItemStacksEqual(stack, stack1))) {
            if (!stack1.isEmpty() &! world.isRemote) entityDropItem(stack1, 0);
            owner = player;
            stack1 = stack.copy();
            stack1.setCount(1);
            setHeldItem(EnumHand.MAIN_HAND, stack1);
            player.setActiveHand(hand);
            player.swingArm(hand);
            stack.shrink(1);
            return true;
        }
        return false;
    }
    
    public float getHoldingItemAnimationProgress(float pt) {
        return MathUtils.lerp(pt, holdingItemAnimationTicks0, holdingItemAnimationTicks) / 5f;
    }
    
    public void duplicate() {
        EntityAllay allay = new EntityAllay(world);
        allay.setPosition(posX, posY, posZ);
        allay.enablePersistence();
        allay.resetDuplicationCooldown();
        resetDuplicationCooldown();
    }
    
    private void resetDuplicationCooldown() {
        dataManager.set(CAN_DUPLICATE, false);
        duplicationCooldown = 6000;
    }
    
    private boolean isDuplicationItem(ItemStack stack) {
        return stack.getItem() == Items.EMERALD;
    }
    
    private boolean canDuplicate() {
        return dataManager.get(CAN_DUPLICATE);
    }
    
    public boolean isDancing() {
        return dataManager.get(IS_DANCING);
    }
    
    public boolean canDance() {
        return jukebox != null && jukebox.distanceSq(posX, posY, posZ) <= 100 && world.getBlockState(jukebox).getBlock() == Blocks.JUKEBOX;
    }
    
    @Nullable
    @Override
    public UUID getOwnerId() {
        return owner == null ? null : owner.getUniqueID();
    }
    
    @Nullable
    @Override
    public Entity getOwner() {
        return owner;
    }
    
    public float getSpinningProcess(float f2) {
        return f2;
    }
    
    public boolean isSpinning() {
        return false;
    }
    
    @Override
    public void fall(float distance, float damageMultiplier) {}
    
    @Override
    protected void playStepSound(BlockPos pos, Block block) {}
    
    @Override
    protected SoundEvent getAmbientSound() {
        return hasItemInSlot(EntityEquipmentSlot.MAINHAND) ? RaidsSoundEvents.ALLAY_AMBIENT_WITH_ITEM
                : RaidsSoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return RaidsSoundEvents.ALLAY_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return RaidsSoundEvents.ALLAY_DEATH;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }
    
}
