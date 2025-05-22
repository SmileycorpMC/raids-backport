package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.util.RaidsLogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class OutpostConfig {

    //generation
    public static int maxDistance;
    public static int distanceFromVillage;
    private static String[] generationBiomesStr;
    private static List<Biome> generationBiomes;
    private static String[] generationBiomesBlacklistStr;
    private static int[] generationDimensions;
    public static int featureCount;
    public static double featureChance;
    public static int featureMinDistance;
    public static int featureMaxDistance;
    
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
            //generation
            maxDistance = config.get("generation", "maxDistance", 32, "Maximum chunk distance between two watchtowers, the lower the number the more likely the generation.").getInt();
            distanceFromVillage = config.get("generation", "distanceFromVillage", 160, "How close can outposts be to villages.").getInt();
            generationBiomesStr = config.get("generation", "generationBiomes", new String[] {"PLAINS", "SANDY", "WASTELAND", "SNOWY", "MOUNTAIN"}, "Which biomes can outposts spawn in (Can specify either biomes names or Biome Dictionaries)?").getStringList();
            generationBiomesBlacklistStr = config.get("generation", "generationBiomesBlacklist", new String[] {"FOREST"}, "Biomes outposts can never spawn in (Overrides generationBiomes, Can specify either biomes names or Biome Dictionaries)?").getStringList();
            generationDimensions = config.get("generation", "generationDimensions", new int[] {0}, "Which dimensions can outposts spawn in?").getIntList();
            featureCount = config.get("generation", "featureCount", 4, "How many features should outposts try to spawn?").getInt();
            featureMinDistance = config.get("generation", "featureMinDistance", 16, "Minimum distance from an outpost features can spawn.").getInt();
            featureMaxDistance = config.get("generation", "featureMaxDistance", 32, "Maximum distance from an outpost features can spawn.").getInt();
            if (featureMaxDistance < featureMinDistance) featureMaxDistance = featureMinDistance;
            //spawns
            maxEntities = config.get("spawns", "maxEntities", 8, "How many entities can be spawned at an outpost at once?").getInt();
            spawnEntitiesStr = config.get("spawns", "spawnEntities", new String[] {"raids:pillager-1"}, "Which entities should spawn in outposts? (format is registry name-spawn weight, weight is any positive integer)").getStringList();
            //chest loot
            ominousBottles = config.get("chest loot", "ominousBottles", true, "Can ominous bottles generate in outpost chests? (Requires Ominous Bottles to be enabled in the raids config)").getBoolean();
            crossbowsBackportCrossbows = config.get("chest loot", "crossbowsBackportCrossbows", true, "Can crossbows backport crossbows generate in outpost chests? (Requires Crossbows Backport to be installed)").getBoolean();
            crossbowCrossbows = config.get("chest loot", "crossbowCrossbows", true, "Can crossbow crossbows generate in outpost chests? (Requires Crossbow to be installed)").getBoolean();
            spartansWeaponryCrossbows = config.get("chest loot", "spartansWeaponryCrossbows", true, "Can spartans weaponry crossbows and bolts generate in outpost chests? (Requires Spartan's Weaponry to be installed)").getBoolean();
            tinkersConstructCrossbows = config.get("chest loot", "tinkersConstructCrossbows", true, "Can tinkers construct crossbows generate in outpost chests? (Requires Tinker's Construct to be installed)").getBoolean();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
        File structure = new File(event.getModConfigurationDirectory().getPath() + "/raids/pillager_outpost");
        if (!structure.exists()) copyStructureFiles(structure);
        else if (!structure.toPath().resolve("watchtower.nbt").toFile().exists()) copyStructureFiles(structure);
        else if (!structure.toPath().resolve("watchtower_overgrown.nbt").toFile().exists()) copyStructureFiles(structure);
    }

    private static void copyStructureFiles(File directory) {
        File file = new File(directory, "features");
        file.mkdirs();
        file.mkdir();
        try (FileSystem mod = FileSystems.newFileSystem(OutpostConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                Collections.emptyMap())) {
            Files.find(mod.getPath("config-defaults/pillager_outpost"), Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile())
                    .forEach(path -> copyFileFromMod(path.toString(), directory));
            RaidsLogger.logInfo("Generated structure files.");
        } catch (Exception e) {
            RaidsLogger.logError("Failed to generate structure files.", e);
        }
    }

    private static void copyFileFromMod(String path, File directory) {
        try {
            FileUtils.copyInputStreamToFile(OutpostConfig.class.getResourceAsStream(path),
                    new File(directory, path.replace( "config-defaults/pillager_outpost", "")));
            RaidsLogger.logInfo("Copied file " + path);
        } catch (Exception e) {
            RaidsLogger.logError("Failed to copy file " + path, e);
        }
    }

    public static List<Biome> getGenerationBiomes() {
        if (generationBiomes == null) {
            generationBiomes = Lists.newArrayList();
            for (String str : generationBiomesStr) {
                if (str.contains(":")) {
                    try {
                        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(str));
                        if (biome != null) generationBiomes.add(biome);
                        else RaidsLogger.logError("Biome " + str + " is not registered", new NullPointerException());
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
                else {
                    try {
                        BiomeDictionary.Type type = BiomeDictionary.Type.getType(str);
                        for (Biome biome : BiomeDictionary.getBiomes(type)) generationBiomes.add(biome);
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
            }
            for (String str : generationBiomesBlacklistStr) {
                if (str.contains(":")) {
                    try {
                        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(str));
                        if (biome != null) generationBiomes.remove(biome);
                        else RaidsLogger.logError("Biome " + str + " is not registered", new NullPointerException());
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
                else {
                    try {
                        BiomeDictionary.Type type = BiomeDictionary.Type.getType(str);
                        for (Biome biome : BiomeDictionary.getBiomes(type)) generationBiomes.remove(biome);
                    } catch (Exception e) {
                        RaidsLogger.logError(str + " is not a valid registry name", e);
                    }
                }
            }
            RaidsLogger.logInfo("Registered outpost biomes " + generationBiomes);
        }
       return generationBiomes;
    }

    public static boolean canGenerateInDimension(int id) {
        for (int i : generationDimensions) if (i == id) return true;
        return false;
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
