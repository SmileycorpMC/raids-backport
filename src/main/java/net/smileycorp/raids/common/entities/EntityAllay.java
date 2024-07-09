package net.smileycorp.raids.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
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
