package net.smileycorp.raids.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.raids.common.capability.Raid;
import net.smileycorp.raids.common.capability.Raider;
import net.smileycorp.raids.common.enchantment.EnchantmentMultishot;
import net.smileycorp.raids.common.enchantment.EnchantmentPiercing;
import net.smileycorp.raids.common.enchantment.EnchantmentQuickCharge;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.item.ItemCrossbow;
import net.smileycorp.raids.common.potion.PotionBadOmen;
import net.smileycorp.raids.common.potion.PotionHeroOfTheVillage;

import java.util.Random;

@EventBusSubscriber(modid = Constants.MODID)
public class RaidsContent {
	
	@CapabilityInject(Raid.class)
	public static Capability<Raid> RAID_CAPABILITY = null;
	
	@CapabilityInject(Raider.class)
	public static Capability<Raider> RAIDER_CAPABILITY = null;
	
	public static final Item CROSSBOW = new ItemCrossbow();
	
	public static final ItemStack OMINOUS_BANNER = createOminousBanner();
	
	private static int ID = 100;
	public static final EntityEntry PILLAGER = EntityEntryBuilder.create().entity(EntityPillager.class).id(Constants.loc("pillager"), ID++).name(Constants.name("Pillager")).egg(5451574, 9804699)
			.tracker(64, 3, true).build();
	public static EntityEntry RAVAGER = EntityEntryBuilder.create().entity(EntityRavager.class).id(Constants.loc("ravager"), ID++).name(Constants.name("Ravager")).egg(7697520, 5984329)
			.tracker(64, 3, true).build();
	
	public static final Potion BAD_OMEN = new PotionBadOmen();
	public static final Potion HERO_OF_THE_VILLAGE = new PotionHeroOfTheVillage();

	public static final EnumEnchantmentType CROSSBOW_ENCHANTMENTS = EnumHelper.addEnchantmentType("crossbow", item -> item == CROSSBOW);

	public static Enchantment QUICK_CHARGE = new EnchantmentQuickCharge();
	public static Enchantment MULTISHOT = new EnchantmentMultishot();
	public static Enchantment PIERCING = new EnchantmentPiercing();
	
	public static ItemStack createOminousBanner() {
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
		ITextComponent name = new TextComponentTranslation("item."+ Constants.name("OminousBanner.name"));
		name.setStyle(new Style().setColor(TextFormatting.GOLD).setItalic(true));
		banner.setTranslatableName(name.getFormattedText());
		banner.getTagCompound().setInteger("HideFlags", 32);
		return banner;
	}
	
	public static ItemStack getVillagerFirework(Random rand) {
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

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(CROSSBOW);
	}

	@SubscribeEvent
	public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
		IForgeRegistry<Enchantment> registry = event.getRegistry();
		registry.register(QUICK_CHARGE);
		registry.register(MULTISHOT);
		registry.register(PIERCING);
	}

	@SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> registry = event.getRegistry();
		registry.register(HERO_OF_THE_VILLAGE);
		registry.register(BAD_OMEN);
    }
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> registry = event.getRegistry();
		registry.register(PILLAGER);
		registry.register(RAVAGER);
	}
	
}
