package net.smileycorp.raids.common;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class Constants {
	
	public static final String NAME = "Raids";
	public static final String MODID = "raids";
	public static final String VERSION = "1.1.4c";
	public static final String DEPENDENCIES = "after:crossbows;after:futuremc;required-after:atlaslib@1.1.7";
	public static final String PATH = "net.smileycorp.raids.";
	public static final String CLIENT = PATH + "client.ClientProxy";
	public static final String SERVER = PATH + "common.CommonProxy";
	
	public static final ResourceLocation PILLAGER_DROPS = loc("entities/pillager");
	public static final ResourceLocation RAVAGER_DROPS = loc("entities/ravager");
	public static final ResourceLocation OUTPOST_CHESTS = loc("chests/pillager_outpost");

    public static final ResourceLocation OVERLEVELED = loc("overleveled");

    public static String name(String name) {
		return MODID + "." + name.replace("_", "");
	}
	
	public static ResourceLocation loc(String name) {
		return new ResourceLocation(MODID, name.toLowerCase());
	}
    
    public static String locStr(String name) {
        return MODID + ":" + name.toLowerCase();
    }
    
    public static ItemStack ominousBanner() {
        NBTTagList patterns = new NBTTagList();
        String[] shapes = {"mr", "bs", "cs", "ms", "hh", "mc", "bo"};
        int[] colours = {6, 7, 8, 0, 7, 7, 0};
        for (int i = 0; i < shapes.length; i++) {
            NBTTagCompound pattern = new NBTTagCompound();
            pattern.setString("Pattern", shapes[i]);
            pattern.setInteger("Color", colours[i]);
            patterns.appendTag(pattern);
        }
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.WHITE, patterns);
        banner.setTranslatableName("item."+ name("OminousBanner.name"));
        banner.getTagCompound().setInteger("HideFlags", 32);
        return banner;
    }
    
    public static ItemStack villagerFirework(Random rand) {
        ItemStack firework = new ItemStack(Items.FIREWORKS);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound fireworks = new NBTTagCompound();
        fireworks.setByte("Flight", (byte)(rand.nextInt(3)+1));
        NBTTagList explosions = new NBTTagList();
        NBTTagCompound explosion = new NBTTagCompound();
        explosion.setByte("Type", (byte)4);
        NBTTagIntArray colours = new NBTTagIntArray(new int[]{ItemDye.DYE_COLORS[rand.nextInt(ItemDye.DYE_COLORS.length)]});
        explosion.setTag("Colors", colours);
        explosions.appendTag(explosion);
        fireworks.setTag("Explosions", explosions);
        nbt.setTag("Fireworks", fireworks);
        firework.setTagCompound(nbt);
        return firework;
    }
    
}
