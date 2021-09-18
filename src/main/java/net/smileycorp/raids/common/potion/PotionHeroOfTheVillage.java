package net.smileycorp.raids.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.common.ModDefinitions;
import net.smileycorp.raids.common.RaidsContent;

public class PotionHeroOfTheVillage extends Potion {

	public PotionHeroOfTheVillage() {
		super(false, 0x44FF44);
		setIconIndex(0, 0);
		setPotionName("effect.raids.hero_of_the_village");
		setRegistryName(ModDefinitions.getResource("hero_of_the_village"));
	}
	
	@Override
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(RaidsContent.POTION_ATLAS);
        return super.getStatusIconIndex();
    }

}
