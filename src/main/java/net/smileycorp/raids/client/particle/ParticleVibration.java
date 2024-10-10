package net.smileycorp.raids.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class ParticleVibration extends Particle {
    
    public static TextureAtlasSprite SPRITE;
    
    public ParticleVibration(World world, double x, double y, double z, double targetX, double targetY, double targetZ) {
        super(world, x, y, z, 0, 0, 0);
        setMaxAge(20);
        setPosition(x, y, z);
        this.motionX = (targetX - x) * 0.05;
        this.motionY = (targetY - y) * 0.05;
        this.motionZ = (targetZ - z) * 0.05;
        prevParticleAngle = particleAngle = (float) Math.atan2(targetX, targetZ);
        canCollide = false;
        particleTexture = SPRITE;
        particleTextureJitterX = 0;
        particleTextureJitterY = 0;
        particleTextureIndexX = 0;
        particleTextureIndexY = 0;
        particleScale = 5;
    }
    
    @Override
    public int getFXLayer() {
        return 1;
    }
    
}
