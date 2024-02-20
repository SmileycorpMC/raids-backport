package net.smileycorp.raids.mixin;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.raid.WorldDataRaids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIMoveIndoors.class)
public class MixinEntityAIMoveIndoors {
    
    @Shadow @Final public EntityCreature entity;
    
    @Shadow public int insidePosX;
    
    @Shadow public int insidePosZ;
    
    @Shadow public VillageDoorInfo doorInfo;
    
    @Inject(at = @At("HEAD"), method = "shouldExecute", cancellable = true)
    public void shouldExecute(CallbackInfoReturnable<Boolean> callback) {
        BlockPos pos = new BlockPos(entity);
        if (WorldDataRaids.getData((WorldServer) entity.world).getRaidAt(pos) == null) return;
        if (entity.getRNG().nextInt(50) != 0) {
            callback.setReturnValue(false);
            return;
        }
        else if (insidePosX != -1 && this.entity.getDistanceSq(insidePosX, this.entity.posY, insidePosZ) < 4.0D) {
            callback.setReturnValue(false);
            return;
        }
        Village village = this.entity.world.getVillageCollection().getNearestVillage(pos, 14);
        if (village == null) {
            callback.setReturnValue(false);
            return;
        }
        doorInfo = village.getDoorInfo(pos);
        callback.setReturnValue(doorInfo != null);
    }
    
}
