package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.raids.common.entities.EntityAllay;

public class EntityAIAllayPickupItem extends EntityAIBase {
    
    private final EntityAllay allay;
    private BlockPos targetPos = null;
    
    public EntityAIAllayPickupItem(EntityAllay allay) {
        this.allay = allay;
    }
    
    @Override
    public boolean shouldExecute() {
        return !allay.getMoveHelper().isUpdating() &! allay.isFull() && allay.getWantedPos() != null &! allay.world.getEntitiesInAABBexcluding(allay, allay.getEntityBoundingBox().grow(16, 16, 16),
                allay::canPickupItem).isEmpty();
    }
    
    @Override
    public void startExecuting() {
        for (Entity entity : allay.world.getEntitiesInAABBexcluding(allay, allay.getEntityBoundingBox().grow(16, 16, 16),
                                                   allay::canPickupItem)) {
            allay.getMoveHelper().setMoveTo(entity.posX, entity.posY, entity.posZ, 2);
            return;
        }
    }
    
    @Override
    public void updateTask() {
        for (Entity entity : allay.world.getEntitiesInAABBexcluding(allay, allay.getEntityBoundingBox().grow(2, 2, 2),
                allay::canPickupItem)) allay.pickupItem((EntityItem)entity);
    }
}
