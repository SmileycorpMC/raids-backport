package net.smileycorp.raids.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.raids.common.entities.ai.AIMoveRandomFlying;
import net.smileycorp.raids.common.entities.ai.FlyingMoveControl;
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
    
    public EntityAllay(World world) {
        super(world);
        moveHelper = new FlyingMoveControl(this);
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
    public void updateAITasks() {
        super.updateAITasks();
        if (world.isRemote |! isEntityAlive()) return;
        if (ticksExisted % 10 == 0) heal(1);
        if (duplicationCooldown > 0) duplicationCooldown--;
        if (duplicationCooldown == 0 &! canDuplicate()) dataManager.set(CAN_DUPLICATE, true);
    }
    
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack stack1 = getHeldItemMainhand();
        if (isDancing() && isDuplicationItem(stack) && canDuplicate()) {
            stack.shrink(1);
            duplicate();
            player.setActiveHand(hand);
            return true;
        }
        if (!stack.isEmpty() &!(ItemStack.areItemStacksEqual(stack, stack1))) {
            if (!stack1.isEmpty()) entityDropItem(stack1, 0);
            owner = player;
            stack1 = stack.copy();
            stack1.setCount(1);
            setHeldItem(EnumHand.MAIN_HAND, stack1);
            player.setActiveHand(hand);
            stack.shrink(1);
            return true;
        }
        return false;
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
    
    private boolean isDancing() {
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
    
}
