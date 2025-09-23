package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.smileycorp.atlas.api.config.EntityAttributesEntry;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class EntityConfig {
    
    private static Configuration config;
    
    public static EntityAttributesEntry pillager;
    public static boolean crossbowsBackportCrossbows;
    public static boolean crossbowCrossbows;
    public static boolean spartansWeaponryCrossbows;
    public static boolean tinkersConstructCrossbows;
    public static EntityAttributesEntry ravager;
    public static EntityAttributesEntry allay;
    
    public static List<ItemStack> duplicationItems;
    private static Map<Class<? extends EntityLiving>, Float> captainChance;
    
    private static String[] duplicationItemsStr;
    private static String[] captainChanceStr;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
         config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/raids/entities.cfg"));
        try{
            config.load();
            pillager = new EntityAttributesEntry(config, "pillager", 0.35, 32, 5, 24, 0, 0, 0, 0);
            crossbowsBackportCrossbows = config.get("pillager", "crossbowsBackportCrossbows", true, "Can pillagers spawn with crossbows backport crossbows? (Requires Crossbows Backport to be installed)").getBoolean();
            crossbowCrossbows = config.get("pillager", "crossbowCrossbows", true, "Can pillagers spawn with crossbow crossbows? (Requires Crossbow to be installed)").getBoolean();
            spartansWeaponryCrossbows = config.get("pillager", "spartansWeaponryCrossbows", true, "Can pillagers spawn with spartans weaponry crossbows? (Requires Spartan's Weaponry to be installed)").getBoolean();
            tinkersConstructCrossbows = config.get("pillager", "tinkersConstructCrossbows", true, "Can pillagers spawn with tinkers construct crossbows? (Requires Tinker's Construct to be installed)").getBoolean();
            ravager = new EntityAttributesEntry(config, "ravager", 0.3, 32, 12, 100, 0, 0, 0.75, 0);
            allay = new EntityAttributesEntry(config, "allay", 0.1, 48, 2, 20, 0, 0, 0, 0.1);
            duplicationItemsStr = config.get("allay", "duplicationItems", new String[] {"minecraft:emerald"},
                    "Items that can be used to duplicate allays use '*' or leave blank to specify any metadata, can accept nbt tags. eg.minecraft:emerald, deeperdepths:material:1").getStringList();
            captainChanceStr = config.get("general", "captainChance", new String[] {"raids:pillager-0.06",
                    "minecraft:vindication_illager-0.06", "minecraft:evocation_illager-0.06", "minecraft:illusion_illager-0.06"},
                    "What's the chance for entities to be patrol captains (also applies when spawned naturally, format is registry name-chance, chance is any number between 0 and 1)").getStringList();
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
    public static float getCaptainChance(Entity entity) {
        if (captainChance == null) {
            captainChance = Maps.newHashMap();
            for (String str : captainChanceStr) {
                try {
                    Class<?> clazz = null;
                    float chance = 0;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        String[] nameSplit = str.split("-");
                        if (nameSplit.length > 1) {
                            ResourceLocation loc = new ResourceLocation(nameSplit[0]);
                            if (GameData.getEntityRegistry().containsKey(loc)) {
                                clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                                try {
                                    chance = Float.valueOf(nameSplit[1]);
                                } catch (Exception e) {
                                    throw new Exception("Entity " + str + " has chance value " + nameSplit[1] + " which is not a valid float");
                                }
                            } else throw new Exception("Entity " + str + " is not registered");
                        } else throw new Exception("Entry " + str + " is not in the correct format");
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz) && chance > 0) {
                        captainChance.put((Class<? extends EntityLiving>) clazz, chance);
                        RaidsLogger.logInfo("Loaded captain chance " + str + " as " + clazz.getName() + " with chance " + chance);
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    RaidsLogger.logError("Error adding captain chance " + str, e);
                }
            }
        }
        return captainChance.containsKey(entity.getClass()) ? captainChance.get(entity.getClass()) : 0;
    }
    
    public static boolean isDuplicationItem(ItemStack stack) {
        if (stack == null) return false;
        if (duplicationItems == null) {
            duplicationItems = Lists.newArrayList();
            for (String name : duplicationItemsStr) try {
                NBTTagCompound nbt = null;
                if (name.contains("{")) {
                    String nbtstring = name.substring(name.indexOf("{"));
                    name = name.substring(0, name.indexOf("{"));
                    try {
                        NBTTagCompound parsed = JsonToNBT.getTagFromJson(nbtstring);
                        if (parsed != null) nbt = parsed;
                    } catch (Exception e) {
                        RaidsLogger.logError("Error parsing nbt for stack " + name + " " + e.getMessage(), e);
                    }
                }
                String[] nameSplit = name.split(":");
                if (nameSplit.length >= 2) {
                    ResourceLocation loc = new ResourceLocation(nameSplit[0], nameSplit[1]);
                    int meta;
                    try {
                        meta = nameSplit.length > 2 ? (nameSplit[2].equals("*") ? OreDictionary.WILDCARD_VALUE
                                : Integer.parseInt(nameSplit[2])) : OreDictionary.WILDCARD_VALUE;
                    } catch (Exception e) {
                        meta = 0;
                        RaidsLogger.logError("Entry" + name + " has a non integer, non wildcard metadata value", e);
                    }
                    if (ForgeRegistries.ITEMS.containsKey(loc)) {
                        ItemStack stack1 = new ItemStack(ForgeRegistries.ITEMS.getValue(loc), 1, meta);
                        if (nbt != null) stack1.setTagCompound(nbt);
                        RaidsLogger.logInfo("Loaded duplication item " + stack1);
                        duplicationItems.add(stack1);
                    }
                } else {
                    throw new Exception(name + " is not a valid registry");
                }
            } catch (Exception e) {
                RaidsLogger.logError("Failed loading allay duplication item " + name, e);
            }
        }
        for (ItemStack stack1 : duplicationItems) {
            if (stack1 == null) return false;
            if (OreDictionary.itemMatches(stack1, stack, false)) return true;
        }
        return false;
    }
    
}
