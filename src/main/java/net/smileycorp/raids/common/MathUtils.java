package net.smileycorp.raids.common;

public class MathUtils {
    
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
    
}
