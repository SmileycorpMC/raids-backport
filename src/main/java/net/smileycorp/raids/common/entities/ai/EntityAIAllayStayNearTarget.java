package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.common.entities.EntityAllay;

public class EntityAIAllayStayNearTarget extends EntityAIBase {
    
    private final EntityAllay allay;
    
    public EntityAIAllayStayNearTarget(EntityAllay allay) {
        this.allay = allay;
        setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        Vec3d wantedPos = allay.getWantedPos();
        if (allay.getMoveHelper().isUpdating() || wantedPos == null) return false;
        double dis = allay.getDistanceSq(wantedPos.x, wantedPos.y, wantedPos.z);
        return dis > 256 && dis <= 4094;
    }
    
    @Override
    public void startExecuting() {
        Vec3d wantedPos = allay.getWantedPos();
        allay.getMoveHelper().setMoveTo(wantedPos.x, wantedPos.y, wantedPos.z, 2);
        super.startExecuting();
    }
}
