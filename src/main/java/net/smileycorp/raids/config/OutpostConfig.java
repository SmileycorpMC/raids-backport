package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;
import java.util.List;

public class OutpostConfig {

    //generation
    public static int maxDistance;
    public static int distanceFromVillage;
    private static String[] spawnBiomesStr;
    private static List<Biome> spawnBiomes;
    
    //spawns
    public static int maxEntities;
    private static List<Biome.SpawnListEntry> spawnEntities;
    private static String[] spawnEntitiesStr;
    
    //chest loot
    public static boolean ominousBottles;
    public static boolean crossbowsBackportCrossbows;
    public static boolean crossbowCrossbows;
    public static boolean spartansWeaponryCrossbows;
    public static boolean tinkersConstructCrossbows;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/outposts.cfg"));
        try{
            config.load();
            maxDistance = config.get("generation", "maxDistance", 32, "Maximum chunk distance between two watchtowers, the lower the number the more likely the generation.").getInt();
            distanceFromVillage = config.get("generation", "distanceFromVillage", 160, "How close can outposts be to villages.").getInt();
            spawnBiomesStr = config.get("generation", "spawnBiomes", new String[] {"minecraft:plains", "minecraft:desert", "minecraft:savanna", "minecraft:taiga", "minecraft:ice_flats", "minecraft:taiga_cold"}, "Which biomes can outposts spawn in?").getStringList();
            maxEntities = config.get("spawns", "maxEntities", 8, "How many entities can be spawned at an outpost at once?").getInt();
            spawnEntitiesStr = config.get("spawns", "spawnEntities", new String[] {"raids:pillager-1"}, "Which entities should spawn in outposts? (format is registry name-spawn weight, weight is any positive integer)").getStringList();
            ominousBottles = config.get("chest loot", "ominousBottles", true, "Can ominous bottles generate in outpost chests? (Requires Ominous Bottles to be enabled in the raids config)").getBoolean();
            crossbowsBackportCrossbows = config.get("chest loot", "crossbowsBackportCrossbows", true, "Can crossbows backport crossbows generate in outpost chests? (Requires Crossbows Backport to be installed)").getBoolean();
            crossbowCrossbows = config.get("chest loot", "crossbowCrossbows", true, "Can crossbow crossbows generate in outpost chests? (Requires Crossbow to be installed)").getBoolean();
            spartansWeaponryCrossbows = config.get("chest loot", "spartansWeaponryCrossbows", true, "Can spartans weaponry crossbows and bolts generate in outpost chests? (Requires Spartan's Weaponry to be installed)").getBoolean();
            tinkersConstructCrossbows = config.get("chest loot", "tinkersConstructCrossbows", true, "Can tinkers construct crossbows generate in outpost chests? (Requires Tinker's Construct to be installed)").getBoolean();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
    public static List<Biome> getSpawnBiomes() {
        if (spawnBiomes == null) {
            spawnBiomes = Lists.newArrayList();
            for (String str : spawnBiomesStr) {
                try {
                    Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(str));
                    if (biome != null) spawnBiomes.add(biome);
                    else RaidsLogger.logError("Biome " + str + " is not registered", new NullPointerException());
                } catch (Exception e) {
                    RaidsLogger.logError(str + " is not a valid registry name", e);
                }
            }
        }
       return spawnBiomes;
    }
    
    public static List<Biome.SpawnListEntry> getSpawnEntities() {
        if (spawnEntities == null) {
            spawnEntities = Lists.newArrayList();
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
                        Biome.SpawnListEntry entry = new Biome.SpawnListEntry((Class<? extends EntityLiving>) clazz, weight, 1, 1);
                        spawnEntities.add(entry);
                        RaidsLogger.logInfo("Loaded outpost spawn entity " + entry + " as " + clazz.getName() + " with weight " + weight);
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    RaidsLogger.logError(str + " is not a valid registry name", e);
                }
            }
        }
        return spawnEntities;
    }
    
    public static boolean isSpawnEntity(EntityLivingBase entity) {
        for (Biome.SpawnListEntry entry : OutpostConfig.getSpawnEntities()) if (entry.entityClass == entity.getClass()) return true;
        return false;
    }
    
}
