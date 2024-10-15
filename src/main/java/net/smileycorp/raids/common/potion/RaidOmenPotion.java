package net.smileycorp.raids.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.network.PacketHandler;
import net.smileycorp.raids.common.network.RaidsParticleMessage;
import net.smileycorp.raids.common.util.EnumRaidsParticle;

import java.util.Random;

public class RaidOmenPotion extends RaidsPotion {
    
    private static final ResourceLocation ADVANCED_TEXTURE = Constants.loc("textures/mob_effect/advanced_raid_omen.png");
    
    public RaidOmenPotion() {
        super(true, 0xDE4058, "raid_omen");
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 3 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        Random rand = entity.getRNG();
        if (entity.isInvisible()) return;
        if (rand.nextBoolean()) {
            if (amplifier > 4) entity.world.spawnParticle(EnumParticleTypes.FLAME, entity.posX + (rand.nextDouble() - 0.5D) * (double)entity.width,
                    entity.posY + rand.nextDouble() * (double)entity.height, entity.posZ + (rand.nextDouble() - 0.5D) * (double)entity.width,
                    0 ,0, 0);
            return;
        }
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RaidsParticleMessage(EnumRaidsParticle.RAID_OMEN,
                        entity.posX + (rand.nextDouble() - 0.5D) * (double)entity.width, entity.posY + rand.nextDouble() * (double)entity.height,
                        entity.posZ + (rand.nextDouble() - 0.5D) * (double)entity.width, (double) (amplifier > 4 ? 0xF75E00 : 0xDE4058)),
                new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 32));
    }
    
    protected ResourceLocation getTexture(PotionEffect effect) {
        return effect.getAmplifier() > 4 ? ADVANCED_TEXTURE : super.getTexture(effect);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    protected void renderEffect(PotionEffect effect, int x, int y, float alpha) {
        if (effect.getAmplifier() <= 4) super.renderEffect(effect, x, y, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, alpha);
        Minecraft mc = Minecraft.getMinecraft();
        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x + 1, y + 12, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + 17, y + 12, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + 17, y - 4, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(x + 1, y - 4, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 9, y + 9, 0);
        float t = (float) Math.sin((float) mc.player.ticksExisted / 10f);
        float t2 = (float) Math.sin((float) mc.player.ticksExisted / 5f);
        GlStateManager.scale( 0.95 + 0.05 * t2,0.95 - 0.05 * t2, 1);
        GlStateManager.rotate(2 * t, 0, 0 , 1);
        GlStateManager.color(1, 1, 1, alpha);
        Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(effect));
        Gui.drawScaledCustomSizeModalRect(-9, -9, 0, 0 , 18, 18, 18, 18, 18, 18);
        GlStateManager.popMatrix();
    }
    
}
