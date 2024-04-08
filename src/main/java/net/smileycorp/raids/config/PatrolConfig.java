package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.raids.common.RaidsLogger;
import net.smileycorp.raids.common.raid.RaidHandler;

import java.io.File;
import java.util.List;

public class PatrolConfig {
    
    private static Configuration config;

    private static Pair<Integer, List<Pair<Class<? extends EntityLiving>, Integer>>> spawnEntities;
    private static String[] spawnEntitiesStr;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/patrols.cfg"));
        try{
            config.load();
            spawnEntitiesStr = config.get("spawns", "spawnEntities", new String[] {"raids:pillager-1"}, "Which entities should spawn in patrols? (format is registry name-spawn weight)").getStringList();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
    public static Pair<Integer, List<Pair<Class<? extends EntityLiving>, Integer>>> getSpawnEntities() {
        if (spawnEntities == null) {
            List<Pair<Class<? extends EntityLiving>, Integer>> list = Lists.newArrayList();
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
                        Pair<Class<? extends EntityLiving>, Integer> entry = new Pair(clazz, total);
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
            spawnEntities = new Pair(total, list);
        }
        return spawnEntities;
    }
    
    public static boolean isSpawnEntity(EntityLivingBase entity) {
        for (Biome.SpawnListEntry entry : OutpostConfig.getSpawnEntities()) if (entry.entityClass == entity.getClass()) return true;
        return false;
    }
    
}