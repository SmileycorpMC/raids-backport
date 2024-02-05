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
    
}
