package net.smileycorp.raids.common;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.entities.EntityRavager;
import net.smileycorp.raids.common.items.ItemOminousBottle;
import net.smileycorp.raids.common.potion.BadOmenPotion;
import net.smileycorp.raids.common.potion.RaidsPotion;
import net.smileycorp.raids.common.raid.RaidOmenTracker;
import net.smileycorp.raids.common.raid.Raider;
import net.smileycorp.raids.common.util.RaidsCriterionTrigger;
import net.smileycorp.raids.config.RaidConfig;

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
	public static EntityEntry ALLAY = EntityEntryBuilder.create().entity(EntityAllay.class).id(Constants.loc("allay"), ID++).name(Constants.name("Allay")).egg(56063, 44543)
			.tracker(64, 3, true).build();
	
	public static final Potion BAD_OMEN = new BadOmenPotion();
	public static final Potion HERO_OF_THE_VILLAGE = new RaidsPotion(false, 0x44FF44, "hero_of_the_village");
	public static final Potion RAID_OMEN = new RaidsPotion(true, 0xDE4058, "raid_omen");
	
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
		registry.register(ALLAY);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();
		registry.register(RaidsSoundEvents.RAID_HORN);
		registry.register(RaidsSoundEvents.PILLAGER_AMBIENT);
		registry.register(RaidsSoundEvents.PILLAGER_HURT);
		registry.register(RaidsSoundEvents.PILLAGER_DEATH);
		registry.register(RaidsSoundEvents.PILLAGER_CELEBRATE);
		registry.register(RaidsSoundEvents.RAVAGER_AMBIENT);
		registry.register(RaidsSoundEvents.RAVAGER_ATTACK);
		registry.register(RaidsSoundEvents.RAVAGER_CELEBRATE);
		registry.register(RaidsSoundEvents.RAVAGER_DEATH);
		registry.register(RaidsSoundEvents.RAVAGER_HURT);
		registry.register(RaidsSoundEvents.RAVAGER_STEP);
		registry.register(RaidsSoundEvents.RAVAGER_STUNNED);
		registry.register(RaidsSoundEvents.RAVAGER_ROAR);
		registry.register(RaidsSoundEvents.BAD_OMEN);
		registry.register(RaidsSoundEvents.RAID_OMEN);
		registry.register(RaidsSoundEvents.OMINOUS_BOTTLE_USE);
	}
}
