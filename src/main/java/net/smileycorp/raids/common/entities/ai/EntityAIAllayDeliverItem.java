package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.raids.common.entities.EntityAllay;

public class EntityAIAllayDeliverItem extends EntityAIBase {
    
    private final EntityAllay allay;
    
    public EntityAIAllayDeliverItem(EntityAllay allay) {
        this.allay = allay;
        setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        Vec3d wantedPos = allay.getWantedPos();
        if (allay.getMoveHelper().isUpdating() || wantedPos == null) return false;
        return !allay.getItems().isEmpty() && allay.getDistanceSq(wantedPos.x, wantedPos.y, wantedPos.z) < 4094;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        Vec3d wantedPos = allay.getWantedPos();
        if (wantedPos == null) return false;
        return !allay.getItems().isEmpty() && allay.getDistanceSq(wantedPos.x, wantedPos.y, wantedPos.z) < 4094;
    }
    
    @Override
    public void startExecuting() {
        Vec3d wantedPos = allay.getWantedPos();
        if (allay.getDistanceSq(wantedPos.x, wantedPos.y, wantedPos.z) <= 4) return;
        Vec3d dir = DirectionUtils.getDirectionVec(wantedPos, new Vec3d(allay.posX, allay.posY, allay.posZ));
        allay.getMoveHelper().setMoveTo(wantedPos.x + dir.x * 3, wantedPos.y + dir.x * 3, wantedPos.z + dir.x * 3, 2);
        super.startExecuting();
    }
    
    @Override
    public void updateTask() {
        Vec3d wantedPos = allay.getWantedPos();
        if (allay.getDistanceSq(wantedPos.x, wantedPos.y, wantedPos.z) >= 4) return;
        allay.throwItem();
    }
    
}
