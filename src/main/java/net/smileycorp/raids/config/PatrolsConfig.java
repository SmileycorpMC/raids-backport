package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.raid.RaidHandler;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class PatrolsConfig {

    private static Map.Entry<Integer, List<Map.Entry<Class<? extends EntityLiving>, Integer>>> spawnEntities;
    private static String[] spawnEntitiesStr;
    public static int patrolMinTime;
    public static int patrolMaxDelay;
    public static int patrolChance;
    public static int dayLength;
    public static int firstDay;
    public static int[] spawnDimensions;
    public static int minSpawns;;
    public static float extraSpawnsPerDifficulty;
    private static String[] spawnBiomesBlacklist;
    private static List<Biome> spawnBiomes;
    private static boolean spawnBiomesWhitelist;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/patrols.cfg"));
        try{
            config.load();
            spawnEntitiesStr = config.get("spawns", "spawnEntities", new String[] {"raids:pillager-1"}, "Which entities should spawn in patrols? (format is registry name-spawn weight, weight is a positive integer)").getStringList();
            patrolMinTime = config.get("general", "patrolMinTime", 12000, "Minimum time before the game attempts to spawn patrols per session.").getInt();
            patrolMaxDelay = config.get("general", "patrolMaxDelay", 12000, "Max delay the game can wait to attempt spawn a patrol.").getInt();
            patrolChance = config.get("general", "patrolChance", 5, "Chance for a patrol to spawn (Set to 0 to disable).").getInt();
            dayLength = config.get("general", "dayLength", 24000, "How many ticks are days? (Change if you have a mod that changes this)").getInt();
            firstDay = config.get("general", "firstDay", 5, "How many days of a world being open until patrols can spawn?").getInt();
            spawnDimensions = config.get("general", "spawnDimensions", new int[] {0}, "Dimensions Patrols can spawn in.").getIntList();
            minSpawns = config.getInt("minSpawns", "general", 1, 0, 255, "Minimum number of entities to spawn in a patrol.");
            extraSpawnsPerDifficulty = config.getFloat("extraSpawnsPerDifficulty", "general", 1, 0, 255, "Number of entities to be added to patrols per regional difficulty level (Rounded Up).");
            spawnBiomesBlacklist = config.get("general", "spawnBiomesBlacklist", new String[] {"MUSHROOM"}, "Biomes patrols can't spawn in (Can specify either biomes names or Biome Dictionaries e.g. minecraft:plains, FOREST)?").getStringList();
            spawnBiomesWhitelist = config.getBoolean("spawnBiomesWhitelist", "general", false, "Invert spawnBiomesBlacklist so that patrols can only spawn in the specified biome.");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
    public static Map.Entry<Integer, List<Map.Entry<Class<? extends EntityLiving>, Integer>>> getSpawnEntities() {
        if (spawnEntities == null) {
            List<Map.Entry<Class<? extends EntityLiving>, Integer>> list = Lists.newArrayList();
            int total = 0;
            for (String str : spawnEntitiesStr) {
                try {
                    Class<?> clazz = null;
                    int weight = 0;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        String[] nameSplit = str.split("-");
                        if (nameSplit.length > 1) {
                            ResourceLocation loc = new ResourceLocation(nameSplit[0]);
                            if (GameData.getEntityRegistry().containsKey(loc)) {
                                clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                                try {
                                    weight = Integer.valueOf(nameSplit[1]);
                                } catch (Exception e) {
                                    throw new Exception("Entity " + str + " has weight value " + nameSplit[1] + " which is not a valid integer");
                                }
                            } else throw new Exception("Entity " + str + " is not registered");
                        } else throw new Exception("Entry " + str + " is not in the correct format");
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz) && weight > 0) {
                        total += weight;
                        Map.Entry<Class<? extends EntityLiving>, Integer> entry = new AbstractMap.SimpleEntry(clazz, total);
                        list.add(entry);
                        RaidHandler.addRaider((Class<? extends EntityLiving>) clazz);
                        RaidsLogger.logInfo("Loaded patrol spawn entity " + entry + " as " + clazz.getName() + " with weight " + weight);
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    RaidsLogger.logError("Error adding patrol entry " + str, e);
                }
            }
            spawnEntities = new AbstractMap.SimpleEntry(total, list);
        }
        return spawnEntities;
    }

    public static boolean canSpawnPatrol(Biome biome) {
        if (spawnBiomes == null) {
            spawnBiomes = Lists.newArrayList();
            for (String str : spawnBiomesBlacklist) {
                if (str.contains(":")) {
                    try {
                        Biome biome1 = Biome.REGISTRY.getObject(new ResourceLocation(str));
                        if (biome1 != null) spawnBiomes.add(biome1);
                        else RaidsLogger.logError("Biome " + str + " is not registered", new NullPointerException());
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
                else {
                    try {
                        BiomeDictionary.Type type = BiomeDictionary.Type.getType(str);
                        for (Biome biome1 : BiomeDictionary.getBiomes(type)) spawnBiomes.add(biome1);
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
            }
        }
        return spawnBiomesWhitelist == spawnBiomes.contains(biome);
    }

}