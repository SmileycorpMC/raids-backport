package net.smileycorp.raids.common.entities.ai;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.raid.WorldDataRaids;

import java.util.Set;

public class EntityAIPathfindToRaid extends EntityAIBase {
    
    private final Raider raider;
    private final EntityCreature mob;
    private int recruitmentTick;
    
    public EntityAIPathfindToRaid(Raider raider, EntityCreature mob) {
        this.raider = raider;
        this.mob = mob;
        setMutexBits(1);
    }
    
    @Override
    public boolean shouldExecute() {
        return mob.getAttackTarget() == null &! mob.isBeingRidden() && shouldContinueExecuting();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return raider.hasActiveRaid() &! raider.getCurrentRaid().isOver()
                &! Raid.isVillage(mob.world, mob.getPosition());
    }
    
    public void updateTask() {
        if (!raider.hasActiveRaid()) return;
        Raid raid = raider.getCurrentRaid();
        if (mob.ticksExisted > recruitmentTick) {
            recruitmentTick = mob.ticksExisted + 20;
            recruitNearby(raid);
        }
        if (!mob.hasPath()) {
            BlockPos center = raid.getCenter();
            Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(mob, 15, 4, new Vec3d(center.getX() + 0.5, center.getY(), center.getZ() + 0.5));
            if (vec3 != null) mob.getNavigator().tryMoveToXYZ(vec3.x, vec3.y, vec3.z, 1.0D);
        }
        
    }
    
    private void recruitNearby(Raid raid) {
        if (raid.isActive()) {
            Set<Entity> set = Sets.newHashSet();
            set.addAll(mob.world.getEntitiesInAABBexcluding(mob, mob.getEntityBoundingBox().grow(16.0D), entity -> {
                if (!(entity instanceof EntityLiving && entity.hasCapability(RaidsContent.RAIDER, null))) return false;
                return !entity.getCapability(RaidsContent.RAIDER, null).hasActiveRaid() && WorldDataRaids.canJoinRaid((EntityLiving) entity, raid);
            }));
            for(Entity entity : set) raid.joinRaid(raid.getGroupsSpawned(), (EntityLiving) entity, true);
        }
        
    }
    
}
