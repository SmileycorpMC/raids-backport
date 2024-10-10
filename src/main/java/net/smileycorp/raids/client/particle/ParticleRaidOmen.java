package net.smileycorp.raids.client.particle;

import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class ParticleRaidOmen extends ParticleSpell {
    
    public static TextureAtlasSprite SPRITE;
    
    public ParticleRaidOmen(World world, double x, double y, double z, int colour) {
        super(world, x, y, z, 0, 0, 0);
        setPosition(x, y, z);
        setRBGColorF((colour >> 16) / 255f, (colour >> 8 & 255) / 255f, (colour & 255) / 255f);
        particleTexture = SPRITE;
        particleTextureJitterX = 0;
        particleTextureJitterY = 0;
        particleTextureIndexX = 0;
        particleTextureIndexY = 0;
    }
    
    @Override
    public int getFXLayer() {
        return 1;
    }
    
    @Override
    public void setParticleTextureIndex(int index) {}
    
}
