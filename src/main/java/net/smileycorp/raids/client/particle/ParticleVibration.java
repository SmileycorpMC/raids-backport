package net.smileycorp.raids.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.MathUtils;

public class ParticleVibration extends Particle {
    
    public static TextureAtlasSprite SPRITE;
    private final Vec3d start, end;
    private float rotY;
    private float prevRotYO;
    
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
        prevRotYO = rotY = (float) Math.atan2(end.z - posZ, end.x - posX);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevRotYO = rotY;
        rotY = (float) Math.atan2(end.z - posZ, end.x - posX);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
    
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks);
        float angleY = MathUtils.lerp(partialTicks, prevRotYO, rotY);
        double sin = Math.sin(angleY);
        double cos = Math.cos(angleY);
        Vec3d[] vecs = new Vec3d[]{new Vec3d(-1, 0, -1), new Vec3d(-1, 0, 1),
                new Vec3d(1, 0, 1), new Vec3d(1, 0, -1)};
        for (int i = 0; i < 4; ++i) {
            Vec3d vec = vecs[i].scale(particleScale * 0.1);
            vecs[i] = new Vec3d(vec.x * cos + vec.z * sin, vec.y,
                    vec.z * cos - vec.x * sin);
        }
        float u0 = particleTexture.getMinU();
        float u1 = particleTexture.getMaxU();
        float v0 = particleTexture.getMinV();
        float v1 = particleTexture.getMaxV();
        int i = getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(u1, v1).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(u1, v0).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(u0, v0).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(u0, v1).color(1, 1, 1, 1).lightmap(j, k).endVertex();

        //underside
        /*buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(u0, v1).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(u0, v0).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(u1, v0).color(1, 1, 1, 1).lightmap(j, k).endVertex();
        buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(u1, v1).color(1, 1, 1, 1).lightmap(j, k).endVertex();*/
    }
}
