package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.common.util.MathUtils;

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
        Vec3d dir = MathUtils.getDirection(wantedPos, new Vec3d(allay.posX, allay.posY, allay.posZ));
        allay.getMoveHelper().setMoveTo(wantedPos.x + dir.x * 3, wantedPos.y + dir.x * 3, wantedPos.z + dir.x * 3, 2);
        super.startExecuting();
    }
}
