package net.smileycorp.raids.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.MathUtils;
import net.smileycorp.atlas.api.util.Quaternion;

import java.util.function.Consumer;

public class ParticleVibration extends Particle {
    
    public static TextureAtlasSprite SPRITE;
    private final Vec3d start, end;
    private float yRot;
    private float yRotO;
    
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
        float f = MathHelper.sin(((float)particleAge + partialTicks - ((float)Math.PI * 2f)) * 0.05f) * 2f;
        float f1 = MathUtils.lerp(partialTicks, yRotO, yRot);
        Vec3d YP = new Vec3d(0, 1, 0);
        Vec3d XP = new Vec3d(1, 0, 0);
        renderSignal(buffer, entity, partialTicks, vec -> {
            vec.multiply(new Quaternion(YP, f1, false));
            vec.multiply(new Quaternion(XP, -1.0472f, false));
            vec.multiply(new Quaternion(YP, f, false));
        });
        renderSignal(buffer, entity, partialTicks, vec -> {
            vec.multiply(new Quaternion(YP, -(float)Math.PI + f1, false));
            vec.multiply(new Quaternion(XP, 1.0472f, false));
            vec.multiply(new Quaternion(YP, f, false));
        });
    }
    
    private void renderSignal(BufferBuilder buffer, Entity entity, float partialTicks, Consumer<Quaternion> p_172482_) {
        Vec3d vec3 = entity.getPositionVector();
        float f = (float)(MathUtils.lerp(partialTicks, prevPosX, posX) - vec3.x);
        float f1 = (float)(MathUtils.lerp(partialTicks, prevPosY, posY) - vec3.y);
        float f2 = (float)(MathUtils.lerp(partialTicks, prevPosZ, posZ) - vec3.z);
        Vec3d vector3f = new Vec3d(0.5, 0.5, 0.5);
        vector3f.normalize();
        Quaternion quaternion = new Quaternion(vector3f, 0.0F, true);
        p_172482_.accept(quaternion);
        Vec3d vector3f1 = new Vec3d(-1, -1, 0);
        vector3f1 = quaternion.transformVector(vector3f1);
        Vec3d[] vecs = new Vec3d[]{new Vec3d(-1, -1, 0), new Vec3d(-1, 1, 0),
                new Vec3d(1, 1, 0), new Vec3d(1, -1, 0)};
        float f3 = particleScale;
        for(int i = 0; i < 4; ++i) {
            Vec3d vector3f2 = vecs[i];
            vector3f2 = quaternion.transformVector(vector3f2);
            vector3f2.scale(f3);
            vector3f2.addVector(f, f1, f2);
        }
        float f6 = 0;
        float f7 = 0;
        float f4 = 16;
        float f5 = 16;
        int j = getBrightnessForRender(partialTicks);
        buffer.pos(vecs[0].x, vecs[0].y, vecs[0].z).tex(f7, f5).color(1f, 1f, 1f, 1f).lightmap(j, j).endVertex();
        buffer.pos(vecs[1].x, vecs[1].y, vecs[1].z).tex(f7, f4).color(1f, 1f, 1f, 1f).lightmap(j, j).endVertex();
        buffer.pos(vecs[2].x, vecs[2].y, vecs[2].z).tex(f6, f4).color(1f, 1f, 1f, 1f).lightmap(j, j).endVertex();
        buffer.pos(vecs[3].x, vecs[3].y, vecs[3].z).tex(f6, f5).color(1f, 1f, 1f, 1f).lightmap(j, j).endVertex();
    }
    
}
