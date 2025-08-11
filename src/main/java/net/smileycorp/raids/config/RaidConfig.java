package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RaidConfig {
    
    public static boolean ominousBottles = true;
    public static boolean raidCenteredOnPlayer = true;
    public static int easyWaves = 3;
    public static int normalWaves = 5;
    public static int hardWaves = 7;
    private static String[] captainPriorityStr;
    private static Map<Class<? extends EntityLiving>, Integer> captainPriority;
    private static String[] villagerEntitiesStr;
    private static List<Class<? extends EntityLiving>> villagerEntities;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/raids.cfg"));
        try{
            config.load();
            ominousBottles = config.get("general", "ominousBottles", true, "Use ominous bottles and the version of bad omen from 1.21+?").getBoolean();
            raidCenteredOnPlayer = config.get("general", "raidCenteredOnPlayer", true, "Should raids be centered on the player's location when the raid starts instead of the village center? (1.21+ behaviour)").getBoolean();
            easyWaves = config.get("general", "easyWaves", 3, "How many waves do raids last for on easy mode?").getInt();
            normalWaves = config.get("general", "normalWaves", 5, "How many waves do raids last for on normal mode?").getInt();
            hardWaves = config.get("general", "hardWaves", 7, "How many waves do raids last for on hard mode?").getInt();
            captainPriorityStr = config.get("general", "captainPriority", new String[] {"raids:pillager-1",
                    "minecraft:vindication_illager-3", "minecraft:evocation_illager-2", "minecraft:illusion_illager-2"}, "What's the priority for raid entities spawning as captain. (format is registry name-priority, higher priority entities are picked over lesser ones, a priority of 0 or less cannot be a captain)").getStringList();
            villagerEntitiesStr = config.get("general", "villagerEntities", new String[] {"tektopia:architect", "tektopia:bard", "tektopia:blacksmith",
                    "tektopia:butcher", "tektopia:chef", "tektopia:child", "tektopia:cleric", "tektopia:druid", "tektopia:enchanter", "tektopia:farmer",
                    "tektopia:guard", "tektopia:lumberjack", "tektopia:merchant", "tektopia:miner", "tektopia:nitwit", "tektopia:nomad", "tektopia:rancher",
                    "tektopia:teacher", "tektopia:tradesman", "toroquest:toroquest_guard", "toroquest:toroquest_mage"}, "Which entities are treated by the game as villagers? (Vanilla villagers and entities that extend vanilla villagers do not need to be put here.)").getStringList();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
        
    }
    
    public static int getCaptainPriority(EntityLiving entity) {
        if (captainPriority == null) {
            captainPriority = Maps.newHashMap();
            for (String str : captainPriorityStr) {
                try {
                    Class<?> clazz = null;
                    int priority = 0;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        String[] nameSplit = str.split("-");
                        if (nameSplit.length > 1) {
                            ResourceLocation loc = new ResourceLocation(nameSplit[0]);
                            if (GameData.getEntityRegistry().containsKey(loc)) {
                                clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                                try {
                                    priority = Integer.valueOf(nameSplit[1]);
                                } catch (Exception e) {
                                    throw new Exception("Entity " + str + " has weight value " + nameSplit[1] + " which is not a valid integer");
                                }
                            } else throw new Exception("Entity " + str + " is not registered");
                        } else throw new Exception("Entry " + str + " is not in the correct format");
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz) && priority > 0) {
                        captainPriority.put((Class<? extends EntityLiving>) clazz, priority);
                        RaidHandler.addRaider((Class<? extends EntityLiving>) clazz);
                        RaidsLogger.logInfo("Loaded captain priority entity " + clazz + " as " + clazz.getName() + " with priority " + priority);
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    RaidsLogger.logError("Error adding patrol entry " + str, e);
                }
            }
        }
        return captainPriority.containsKey(entity.getClass()) ? captainPriority.get(entity.getClass()) : 0;
    }
    
    public static boolean isTickableVillager(EntityLivingBase entity) {
        if (villagerEntities == null) {
            villagerEntities = Lists.newArrayList();
            for (String str : villagerEntitiesStr) {
                try {
                    Class<?> clazz = null;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        ResourceLocation loc = new ResourceLocation(str);
                        if (GameData.getEntityRegistry().containsKey(loc)) {
                            clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                        } else continue;
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz)) {
                       villagerEntities.add((Class<? extends EntityLiving>) clazz);
                        RaidsLogger.logInfo("Loaded villager " + clazz + " as " + clazz.getName());
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    RaidsLogger.logError("Error adding villager " + str, e);
                }
            }
        }
        if (!(entity instanceof EntityLiving) || entity instanceof EntityVillager) return false;
        for (Class<? extends EntityLiving> clazz : villagerEntities) if (clazz.isAssignableFrom(entity.getClass())) return true;
        return false;
    }
    
}