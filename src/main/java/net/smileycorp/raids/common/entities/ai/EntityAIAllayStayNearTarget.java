package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
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
        BlockPos wantedPos = allay.getWantedPos();
        if (allay.getMoveHelper().isUpdating() || wantedPos == null) return false;
        double dis = allay.getDistanceSq(wantedPos);
        return dis > 256 && dis <= 4094;
    }
    
    @Override
    public void startExecuting() {
        Vec3d wantedPos;
        BlockPos noteBlock = allay.getNoteBlockPos();
        if (noteBlock != null) wantedPos = new Vec3d(noteBlock.getX() + 0.5, noteBlock.getY() + 0.5, noteBlock.getZ() + 0.5);
        else wantedPos = new Vec3d(allay.getOwner().posX, allay.getOwner().posY + allay.getOwner().getEyeHeight(), allay.getOwner().posZ);
        allay.getMoveHelper().setMoveTo(wantedPos.x, wantedPos.y, wantedPos.z, 2);
        super.startExecuting();
    }
}
