package net.smileycorp.raids.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.*;
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
import net.smileycorp.raids.common.capability.Raid;
import net.smileycorp.raids.common.capability.Raider;
import net.smileycorp.raids.common.entities.EntityPillager;
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
	public static EntityEntry RAVAGER /*= EntityEntryBuilder.create().entity(EntityRavager.class).id(ModDefinitions.getResource("ravager"), ID++).name(ModDefinitions.getName("Ravager")).egg(7697520, 5984329)
			.tracker(64, 3, true).build()*/;
	
	public static final Potion BAD_OMEN = new PotionBadOmen();
	public static final Potion HERO_OF_THE_VILLAGE = new PotionHeroOfTheVillage();
	
	public static Enchantment QUICK_CHARGE;
	public static Enchantment MULTISHOT;
	public static Enchantment Piercing;

	public static final SoundEvent CROSSBOW_HIT = new SoundEvent(Constants.loc("item.crossbow.hit"));
	public static final SoundEvent CROSSBOW_LOADING_END = new SoundEvent(Constants.loc("item.crossbow.loading_end"));
	public static final SoundEvent CROSSBOW_LOADING_MIDDLE = new SoundEvent(Constants.loc("item.crossbow.loading_middle"));
	public static final SoundEvent CROSSBOW_LOADING_START = new SoundEvent(Constants.loc("item.crossbow.loading_start"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_1 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_1"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_2 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_2"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_3 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_3"));
	public static final SoundEvent CROSSBOW_SHOOT = new SoundEvent(Constants.loc("item.crossbow.shoot"));
	public static final SoundEvent PILLAGER_AMBIENT = new SoundEvent(Constants.loc("entity.pillager.ambient"));
	public static final SoundEvent PILLAGER_HURT = new SoundEvent(Constants.loc("entity.pillager.hurt"));
	public static final SoundEvent PILLAGER_DEATH = new SoundEvent(Constants.loc("entity.pillager.death"));
	public static final SoundEvent PILLAGER_CELEBRATE = new SoundEvent(Constants.loc("entity.pillager.celebrate"));
	public static final SoundEvent RAVAGER_AMBIENT = new SoundEvent(Constants.loc("entity.ravager.ambient"));
	public static final SoundEvent RAVAGER_ATTACK = new SoundEvent(Constants.loc("entity.ravager.attack"));
	public static final SoundEvent RAVAGER_CELEBRATE = new SoundEvent(Constants.loc("entity.ravager.celebrate"));
	public static final SoundEvent RAVAGER_DEATH = new SoundEvent(Constants.loc("entity.ravager.death"));
	public static final SoundEvent RAVAGER_HURT = new SoundEvent(Constants.loc("entity.ravager.hurt"));
	public static final SoundEvent RAVAGER_STEP = new SoundEvent(Constants.loc("entity.ravager.step"));
	public static final SoundEvent RAVAGER_STUNNED = new SoundEvent(Constants.loc("entity.ravager.stunned"));
	public static final SoundEvent RAVAGER_ROAR = new SoundEvent(Constants.loc("entity.ravager.roar"));
	public static final SoundEvent RAID_HORN = new SoundEvent(Constants.loc("event.raid.horn"));
	
	public static final ResourceLocation PILLAGER_DROPS = Constants.loc("entities.pillager");
	public static final ResourceLocation RAVAGER_DROPS = Constants.loc("entities.ravager");
	public static final ResourceLocation OUTPOST_CHESTS = Constants.loc("chests.pillager_outpost");
	public static final ResourceLocation POTION_ATLAS = Constants.loc("textures/gui/potions.png");
	
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
