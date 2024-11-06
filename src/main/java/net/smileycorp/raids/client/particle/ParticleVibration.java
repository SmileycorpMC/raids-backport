package net.smileycorp.raids.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.DirectionUtils;

public class ParticleVibration extends Particle {
    
    public static TextureAtlasSprite SPRITE;
    private final Vec3d start, end;
    
    public ParticleVibration(World world, double x, double y, double z, double targetX, double targetY, double targetZ) {
        super(world, x, y, z, 0, 0, 0);
        setMaxAge(20);
        setPosition(x, y, z);
        this.motionX = (targetX - x) * 0.05;
        this.motionY = (targetY - y) * 0.05;
        this.motionZ = (targetZ - z) * 0.05;
        start = new Vec3d(x, y, z);
        end = new Vec3d(targetX, targetY, targetZ);
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
    
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft mc = Minecraft.getMinecraft();
        float fov = mc.entityRenderer.getFOVModifier(partialTicks, true);
        Vec3d pos = entity.getPositionEyes(partialTicks);
        float yaw = entity.rotationYaw + (entity.prevRotationYaw - entity.rotationYaw) * partialTicks;
        float pitch = entity.rotationPitch + (entity.prevRotationPitch - entity.rotationPitch) * partialTicks;
        Vec2f start = DirectionUtils.getProjectedPos(this.start, pos, yaw, pitch, mc.displayWidth, mc.displayHeight, fov);
        Vec2f end = DirectionUtils.getProjectedPos(this.end, pos, yaw, pitch, mc.displayWidth, mc.displayHeight, fov);
        particleAngle = (float) Math.atan2(end.y - start.y, end.x - start.x) + 1.570796f;
        prevParticleAngle = particleAngle;
        super.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
    
}
