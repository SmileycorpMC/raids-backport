package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIMoveRandomFlying extends EntityAIBase {

    private final EntityLiving entity;

    public EntityAIMoveRandomFlying(EntityLiving entity) {
        setMutexBits(1);
        this.entity = entity;
    }

    public boolean shouldExecute() {
        return !entity.getMoveHelper().isUpdating() && entity.getRNG().nextInt(7) == 0;
    }

    public boolean shouldContinueExecuting() {
        return false;
    }

    public void updateTask() {
        BlockPos blockpos = new BlockPos(entity);
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(entity.getRNG().nextInt(15) - 7, entity.getRNG().nextInt(11) - 5, entity.getRNG().nextInt(15) - 7);
            if (entity.world.isAirBlock(blockpos1)) {
                entity.getMoveHelper().setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (entity.getAttackTarget() == null)
                    entity.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0F, 20.0F);
                break;
            }
        }

    }
}
