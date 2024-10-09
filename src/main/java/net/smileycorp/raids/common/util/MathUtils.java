package net.smileycorp.raids.common.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
    
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }
    
    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
    
    public static float wrap(float val, float deviation) {
        return (Math.abs(val % deviation - deviation * 0.5F) - deviation * 0.25F) / (deviation * 0.25F);
    }
    
    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }
    
    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }
    
    public static void throwItem(EntityLivingBase entity, ItemStack stack, Vec3d target) {
        EntityItem item = new EntityItem(entity.world, entity.posX, entity.posY + entity.getEyeHeight() - 0.3, entity.posZ, stack);
        item.setThrower(entity.getUniqueID().toString());
        Vec3d vel = target.subtract(item.posX, item.posY, item.posZ)
                .normalize().scale(0.3);
        item.motionX = vel.x;
        item.motionY = vel.y;
        item.motionZ = vel.z;
        item.setDefaultPickupDelay();
        entity.world.spawnEntity(item);
    }
    
    public static Vec3d centerOf(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
    
    public static Vec3d getDirection(Vec3d startpos, Vec3d endpos) {
        if (startpos.equals(endpos)) return new Vec3d(0,0,0);
        double dx = endpos.x-startpos.x;
        double dy = endpos.y-startpos.y;
        double dz = endpos.z-startpos.z;
        double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        double mx = (endpos.x-startpos.x)/magnitude;
        double my = (endpos.y-startpos.y)/magnitude;
        double mz = (endpos.z-startpos.z)/magnitude;
        return new Vec3d(mx, my, mz);
    }
    
}
