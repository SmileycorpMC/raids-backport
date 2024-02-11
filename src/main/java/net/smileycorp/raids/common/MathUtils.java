package net.smileycorp.raids.common;

public class MathUtils {
    
    public static float lerp(float p_14180_, float p_14181_, float p_14182_) {
        return p_14181_ + p_14180_ * (p_14182_ - p_14181_);
    }
    
    public static double lerp(double p_14180_, double p_14181_, double p_14182_) {
        return p_14181_ + p_14180_ * (p_14182_ - p_14181_);
    }
    
    public static float triangleWave(float p_217167_1_, float p_217167_2_) {
        return (Math.abs(p_217167_1_ % p_217167_2_ - p_217167_2_ * 0.5F) - p_217167_2_ * 0.25F) / (p_217167_2_ * 0.25F);
    }
    
    public static int clamp(int p_14046_, int p_14047_, int p_14048_) {
        if (p_14046_ < p_14047_) {
            return p_14047_;
        } else {
            return p_14046_ > p_14048_ ? p_14048_ : p_14046_;
        }
    }
    
    public static float clamp(float p_14037_, float p_14038_, float p_14039_) {
        if (p_14037_ < p_14038_) {
            return p_14038_;
        } else {
            return p_14037_ > p_14039_ ? p_14039_ : p_14037_;
        }
    }
    
}
