package net.smileycorp.raids.common;

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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.items.ItemOminousBottle;
import net.smileycorp.raids.common.potion.BadOmenPotion;
import net.smileycorp.raids.common.potion.RaidsPotion;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.util.RaidsCriterionTrigger;
import net.smileycorp.raids.config.RaidConfig;

import java.util.Random;

@EventBusSubscriber(modid = Constants.MODID)
public class RaidsContent {
	
	public static final RaidsCriterionTrigger WHOS_THE_PILLAGER = new RaidsCriterionTrigger("whos_the_pillager");
	public static final RaidsCriterionTrigger VOLUNTARY_EXILE = new RaidsCriterionTrigger("voluntary_exile");
	public static final RaidsCriterionTrigger RAID_VICTORY = new RaidsCriterionTrigger("raid_victory");
    
    @CapabilityInject(Raider.class)
	public static Capability<Raider> RAIDER = null;
	
	@CapabilityInject(RaidOmenTracker.class)
	public static Capability<RaidOmenTracker> RAID_OMEN_TRACKER = null;
	
	public static final Item OMINOUS_BOTTLE = new ItemOminousBottle();
	
	private static int ID = 154;
	public static final EntityEntry PILLAGER = EntityEntryBuilder.create().entity(EntityPillager.class).id(Constants.loc("pillager"), ID++).name(Constants.name("Pillager")).egg(5451574, 9804699)
			.tracker(64, 3, true).build();
	public static EntityEntry RAVAGER = EntityEntryBuilder.create().entity(EntityRavager.class).id(Constants.loc("ravager"), ID++).name(Constants.name("Ravager")).egg(7697520, 5984329)
			.tracker(64, 3, true).build();
	
	public static final Potion BAD_OMEN = new BadOmenPotion();
	public static final Potion HERO_OF_THE_VILLAGE = new RaidsPotion(false, 0x44FF44, "hero_of_the_village");
	public static final Potion RAID_OMEN = new RaidsPotion(true, 0xDE4058, "raid_omen");
	
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
		IForgeRegistry<Item> registry = event.getRegistry();
		if (RaidConfig.ominousBottles) registry.register(OMINOUS_BOTTLE);
	}

	@SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> registry = event.getRegistry();
		registry.register(HERO_OF_THE_VILLAGE);
		registry.register(BAD_OMEN);
		if (RaidConfig.ominousBottles) registry.register(RAID_OMEN);
    }
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> registry = event.getRegistry();
		registry.register(PILLAGER);
		registry.register(RAVAGER);
	}
	
}
