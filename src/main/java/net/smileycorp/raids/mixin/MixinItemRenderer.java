package net.smileycorp.raids.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.raids.common.ItemCrossbow;
import net.smileycorp.raids.common.RaidsContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    @Shadow protected abstract void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

    @Shadow @Final private Minecraft mc;

    @Shadow protected abstract void transformFirstPerson(EnumHandSide hand, float p_187453_2_);

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        if (stack.getItem() != RaidsContent.CROSSBOW) return;
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();
        boolean flag1 = ItemCrossbow.isCharged(stack);
        boolean flag2 = enumhandside == EnumHandSide.RIGHT;
        int i = flag2 ? 1 : -1;
        if (!player.getActiveItemStack().isEmpty() && player.getItemInUseMaxCount() > 0 && player.getActiveHand() == hand) {
            transformSideFirstPerson(enumhandside, p_187457_7_);
            GlStateManager.translate((double)((float)i * -0.4785682F), (double)-0.094387F, (double)0.05731531F);
            GlStateManager.rotate(-11.935F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(i * 65.3F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(i * -9.785F, 0.0F, 0.0F, 1.0F);
            float f9 = (float)stack.getMaxItemUseDuration() - ((float)mc.player.getItemInUseCount() - p_187457_2_+ 1.0F);
            float f13 = f9 / (float)ItemCrossbow.getChargeDuration(stack);
            if (f13 > 1.0F) {
                f13 = 1.0F;
            }

            if (f13 > 0.1F) {
                float f16 = MathHelper.sin((f9 - 0.1F) * 1.3F);
                float f3 = f13 - 0.1F;
                float f4 = f16 * f3;
                GlStateManager.translate((double)(f4 * 0.0F), (double)(f4 * 0.004F), (double)(f4 * 0.0F));
            }

            GlStateManager.translate((double)(f13 * 0.0F), (double)(f13 * 0.0F), (double)(f13 * 0.04F));
            GlStateManager.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
            GlStateManager.rotate(i * 45.0F, 0.0F, -1.0F, 0.0F);
        } else {
            float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float)Math.PI);
            float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float)Math.PI * 2F));
            float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float)Math.PI);
            GlStateManager.translate((double)((float)i * f), (double)f1, (double)f2);
            transformSideFirstPerson(enumhandside, p_187457_7_);
            transformFirstPerson(enumhandside, p_187457_5_);
            if (flag1 && p_187457_5_ < 0.001F && flag) {
                GlStateManager.translate((double)((float)i * -0.641864F), 0.0D, 0.0D);
                GlStateManager.rotate(i * 10.0F, 0.0F, 1.0F, 0.0F);
            }
        }
        this.renderItemSide(player, stack, flag2 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag2);
        GlStateManager.popMatrix();
        ci.cancel();
    }

}
