package net.smileycorp.raids.common;

public class MathUtils {
    
    public static double lerp(double p_14180_, double p_14181_, double p_14182_) {
        return p_14181_ + p_14180_ * (p_14182_ - p_14181_);
    }
    
    public static float triangleWave(float p_217167_1_, float p_217167_2_) {
        return (Math.abs(p_217167_1_ % p_217167_2_ - p_217167_2_ * 0.5F) - p_217167_2_ * 0.25F) / (p_217167_2_ * 0.25F);
    }
    
    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }
    
    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }
    
}
