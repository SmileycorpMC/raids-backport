package net.smileycorp.raids.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;

public class PotionBadOmen extends Potion {

	public PotionBadOmen() {
		super(true, 0x0b6138);
		setIconIndex(1, 0);
		setPotionName("effect.raids.bad_omen");
		setRegistryName(Constants.loc("bad_omen"));
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
