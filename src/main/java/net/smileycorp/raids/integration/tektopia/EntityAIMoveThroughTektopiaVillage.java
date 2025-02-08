package net.smileycorp.raids.integration.tektopia;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.structures.VillageStructure;

import java.util.Iterator;
import java.util.List;

public class EntityAIMoveThroughTektopiaVillage extends EntityAIBase {
    private final EntityCreature entity;
    private final double movementSpeed;
    private Path path;
    private BlockPos doorInfo;
    private final boolean isNocturnal;
    private final List<BlockPos> doorList = Lists.newArrayList();

    public EntityAIMoveThroughTektopiaVillage(EntityCreature p_i1638_1_, double p_i1638_2_, boolean p_i1638_4_) {
        this.entity = p_i1638_1_;
        this.movementSpeed = p_i1638_2_;
        this.isNocturnal = p_i1638_4_;
        this.setMutexBits(1);
        if (!(p_i1638_1_.getNavigator() instanceof PathNavigateGround)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    public boolean shouldExecute() {
        this.resizeDoorList();
        if (this.isNocturnal && this.entity.world.isDaytime()) {
            return false;
        } else {
           // Village lvt_1_1_ = this.entity.world.getVillageCollection().getNearestVillage(new BlockPos(this.entity), 0);
            Village lvt_1_1_ = VillageManager.get(this.entity.world).getVillageAt(this.entity.getPosition());
            if (lvt_1_1_ == null) {
                return false;
            } else {
                this.doorInfo = this.findNearestDoor(lvt_1_1_);
                if (this.doorInfo == null) {
                    return false;
                } else {
                    PathNavigateGround lvt_2_1_ = (PathNavigateGround)this.entity.getNavigator();
                    boolean lvt_3_1_ = lvt_2_1_.getEnterDoors();
                    lvt_2_1_.setBreakDoors(false);
                    this.path = lvt_2_1_.getPathToPos(this.doorInfo);
                    lvt_2_1_.setBreakDoors(lvt_3_1_);
                    if (this.path != null) {
                        return true;
                    } else {
                        Vec3d lvt_4_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 10, 7, new Vec3d((double)this.doorInfo.getX(), (double)this.doorInfo.getY(), (double)this.doorInfo.getZ()));
                        if (lvt_4_1_ == null) {
                            return false;
                        } else {
                            lvt_2_1_.setBreakDoors(false);
                            this.path = this.entity.getNavigator().getPathToXYZ(lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z);
                            lvt_2_1_.setBreakDoors(lvt_3_1_);
                            return this.path != null;
                        }
                    }
                }
            }
        }
    }

    public boolean shouldContinueExecuting() {
        if (this.entity.getNavigator().noPath()) {
            return false;
        } else {
            float lvt_1_1_ = this.entity.width + 4.0F;
            return this.entity.getDistanceSq(this.doorInfo) > (double)(lvt_1_1_ * lvt_1_1_);
        }
    }

    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, this.movementSpeed);
    }

    public void resetTask() {
        if (this.entity.getNavigator().noPath() || this.entity.getDistanceSq(this.doorInfo) < 16.0) {
            this.doorList.add(this.doorInfo);
        }

    }

    private BlockPos findNearestDoor(Village p_75412_1_) {
        BlockPos doorPos = null;

        int lvt_3_1_ = Integer.MAX_VALUE;

        Village vil = VillageManager.get(this.entity.world).getVillageAt(this.entity.getPosition());
        List<VillageStructure> structures = vil.getHomes();
        for(VillageStructure structure : structures)
        {
            int distance = (int)structure.getDoor().distanceSq(MathHelper.floor(this.entity.posX), MathHelper.floor(this.entity.posY), MathHelper.floor(this.entity.posZ));
            if(distance < lvt_3_1_ && !this.doesDoorListContain(structure.getDoor()))
            {
                doorPos = structure.getDoor();
                lvt_3_1_ = distance;
            }
        }

        return doorPos;
    }

    private boolean doesDoorListContain(BlockPos p_75413_1_) {
        Iterator var2 = this.doorList.iterator();

        BlockPos lvt_3_1_;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            lvt_3_1_ = (BlockPos)var2.next();
        } while(!p_75413_1_.equals(lvt_3_1_));

        return true;
    }

    private void resizeDoorList() {
        if (this.doorList.size() > 15) {
            this.doorList.remove(0);
        }

    }
}

