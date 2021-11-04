package net.smileycorp.raids.common;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
import net.smileycorp.raids.common.capability.IRaid;
import net.smileycorp.raids.common.capability.IRaider;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.potion.PotionBadOmen;
import net.smileycorp.raids.common.potion.PotionHeroOfTheVillage;

@EventBusSubscriber(modid = ModDefinitions.MODID)
public class RaidsContent {
	
	@CapabilityInject(IRaid.class)
	public static Capability<IRaid> RAID_CAPABILITY = null;
	
	@CapabilityInject(IRaider.class)
	public static Capability<IRaider> RAIDER_CAPABILITY = null;
	
	public static final Item CROSSBOW = new ItemCrossbow();
	
	public static final ItemStack OMINOUS_BANNER = createOminousBanner();
	
	private static int ID = 100;
	public static final EntityEntry PILLAGER = EntityEntryBuilder.create().entity(EntityPillager.class).id(ModDefinitions.getResource("pillager"), ID++).name(ModDefinitions.getName("Pillager")).egg(5451574, 9804699)
			.tracker(64, 3, true).build();
	public static EntityEntry RAVAGER /*= EntityEntryBuilder.create().entity(EntityRavager.class).id(ModDefinitions.getResource("ravager"), ID++).name(ModDefinitions.getName("Ravager")).egg(7697520, 5984329)
			.tracker(64, 3, true).build()*/;
	
	public static final Potion BAD_OMEN = new PotionBadOmen();
	public static final Potion HERO_OF_THE_VILLAGE = new PotionHeroOfTheVillage();
	
	public static Enchantment QUICK_CHARGE;
	public static Enchantment MULTISHOT;
	public static Enchantment Piercing;
	
	public static final SoundEvent CROSSBOW_LOADING_END = new SoundEvent(ModDefinitions.getResource("item.crossbow.loading_end"));
	public static final SoundEvent CROSSBOW_LOADING_MIDDLE = new SoundEvent(ModDefinitions.getResource("item.crossbow.loading_middle"));
	public static final SoundEvent CROSSBOW_LOADING_START = new SoundEvent(ModDefinitions.getResource("item.crossbow.loading_start"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_1 = new SoundEvent(ModDefinitions.getResource("item.crossbow.quick_charge_1"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_2 = new SoundEvent(ModDefinitions.getResource("item.crossbow.quick_charge_2"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_3 = new SoundEvent(ModDefinitions.getResource("item.crossbow.quick_charge_3"));
	public static final SoundEvent CROSSBOW_SHOOT = new SoundEvent(ModDefinitions.getResource("item.crossbow.shoot"));
	public static final SoundEvent PILLAGER_AMBIENT = new SoundEvent(ModDefinitions.getResource("entity.pillager.ambient"));
	public static final SoundEvent PILLAGER_HURT = new SoundEvent(ModDefinitions.getResource("entity.pillager.hurt"));
	public static final SoundEvent PILLAGER_DEATH = new SoundEvent(ModDefinitions.getResource("entity.pillager.death"));
	public static final SoundEvent PILLAGER_CELEBRATE = new SoundEvent(ModDefinitions.getResource("entity.pillager.celebrate"));
	public static final SoundEvent RAVAGER_AMBIENT = new SoundEvent(ModDefinitions.getResource("entity.ravager.ambient"));
	public static final SoundEvent RAVAGER_ATTACK = new SoundEvent(ModDefinitions.getResource("entity.ravager.attack"));
	public static final SoundEvent RAVAGER_CELEBRATE = new SoundEvent(ModDefinitions.getResource("entity.ravager.celebrate"));
	public static final SoundEvent RAVAGER_DEATH = new SoundEvent(ModDefinitions.getResource("entity.ravager.death"));
	public static final SoundEvent RAVAGER_HURT = new SoundEvent(ModDefinitions.getResource("entity.ravager.hurt"));
	public static final SoundEvent RAVAGER_STEP = new SoundEvent(ModDefinitions.getResource("entity.ravager.step"));
	public static final SoundEvent RAVAGER_STUNNED = new SoundEvent(ModDefinitions.getResource("entity.ravager.stunned"));
	public static final SoundEvent RAVAGER_ROAR = new SoundEvent(ModDefinitions.getResource("entity.ravager.roar"));
	public static final SoundEvent RAID_HORN = new SoundEvent(ModDefinitions.getResource("event.raid.horn"));
	
	public static final ResourceLocation PILLAGER_DROPS = ModDefinitions.getResource("entities.pillager");
	public static final ResourceLocation RAVAGER_DROPS = ModDefinitions.getResource("entities.ravager");
	public static final ResourceLocation OUTPOST_CHESTS = ModDefinitions.getResource("chests.pillager_outpost");
	public static final ResourceLocation POTION_ATLAS = ModDefinitions.getResource("textures/gui/potions.png");
	
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
		ITextComponent name = new TextComponentTranslation("item."+ModDefinitions.getName("OminousBanner.name"));
		name.setStyle(new Style().setColor(TextFormatting.GOLD));
		banner.setStackDisplayName(name.getFormattedText());
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
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> registry = event.getRegistry();
		registry.register(HERO_OF_THE_VILLAGE);
		registry.register(BAD_OMEN);
    }
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> registry = event.getRegistry();
		registry.register(PILLAGER);
	}
	
}
