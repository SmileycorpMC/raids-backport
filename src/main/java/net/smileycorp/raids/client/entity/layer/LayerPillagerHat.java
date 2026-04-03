package net.smileycorp.raids.client.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.raids.client.entity.RenderPillager;
import net.smileycorp.raids.client.entity.model.ModelPillager;
import net.smileycorp.raids.client.entity.model.ModelPillagerHat;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.entities.EntityPillager;

public class LayerPillagerHat implements LayerRenderer<EntityPillager> {

    private static final ResourceLocation UPGRADED_1_HAT = Constants.loc("textures/entity/illager/pillager_upgraded1_hat.png");
    private static final ResourceLocation UPGRADED_2_HAT = Constants.loc("textures/entity/illager/pillager_upgraded2_hat.png");

    private final RenderPillager renderer;
    private final ModelPillagerHat hat = new ModelPillagerHat();

    public LayerPillagerHat(RenderPillager renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityPillager pillager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack chest = pillager.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof ItemArmor)) return;
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(
                ((ItemArmor) chest.getItem()).damageReduceAmount > 6 ? UPGRADED_2_HAT : UPGRADED_1_HAT);
        ModelPillager model = (ModelPillager) renderer.getMainModel();
        ModelRenderer head = model.head;
        GlStateManager.pushMatrix();
        GlStateManager.translate(head.offsetX, head.offsetY, head.offsetZ);
        GlStateManager.pushMatrix();
        GlStateManager.translate(head.rotationPointX, head.rotationPointY, head.rotationPointZ);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (head.rotateAngleZ != 0) GlStateManager.rotate(head.rotateAngleZ * (180f / (float)Math.PI), 0, 0, 1);
        if (head.rotateAngleY != 0)  GlStateManager.rotate(head.rotateAngleY * (180f / (float)Math.PI), 0, 1, 0);
        if (head.rotateAngleX != 0) GlStateManager.rotate(head.rotateAngleX * (180f / (float)Math.PI), 1, 0, 0);
        int i = pillager.getBrightnessForRender();
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        hat.render();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
