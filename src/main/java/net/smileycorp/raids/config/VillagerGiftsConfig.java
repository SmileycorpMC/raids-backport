package net.smileycorp.raids.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.common.util.accessors.IVillager;
import net.smileycorp.raids.common.util.accessors.IVillagerProfession;
import net.smileycorp.raids.common.util.accessors.IVillagerRegistry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class VillagerGiftsConfig {
    
    public static VillagerGiftsConfig INSTANCE;
    private final File file;
    private final Map<VillagerRegistry.VillagerCareer, List<ItemStack>> gifts = Maps.newHashMap();
    
    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new VillagerGiftsConfig(new File(event.getModConfigurationDirectory().getPath() + "/raids/villager_gifts.json"));
    }
    
    private VillagerGiftsConfig(File file) {
        this.file = file;
        if (!file.exists()) {
            RaidsLogger.logInfo("Villager gifts file does not exist, generating default data");
            file.getParentFile().mkdirs();
            try {
                FileUtils.copyInputStreamToFile(VillagerGiftsConfig.class.getResourceAsStream("/config-defaults/villager_gifts.json"), file);
            } catch (Exception e) {
                RaidsLogger.logError("Failed generating default villager gifts table", e);
            }
        }
    }
    
    public void loadGifts() {
        JsonParser parser = new JsonParser();
        try {
            for (Map.Entry<String, JsonElement> entry : parser.parse(new FileReader(file)).getAsJsonObject().entrySet()) {
                try {
                    ResourceLocation location = new ResourceLocation(entry.getKey());
                    VillagerRegistry.VillagerProfession profession = ((IVillagerRegistry)VillagerRegistry.instance()).getProfession(location);
                    if (profession == null) throw new NullPointerException("Profession " + location + " not registered");
                    JsonObject json = entry.getValue().getAsJsonObject();
                    List<VillagerRegistry.VillagerCareer> careers = ((IVillagerProfession) profession).getCareers();
                    for (VillagerRegistry.VillagerCareer career : careers) {
                        List<ItemStack> stacks = Lists.newArrayList();
                        try {
                            for (JsonElement element : json.get(career.getName()).getAsJsonArray()) {
                                String name = element.getAsString();
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
                                if (nameSplit.length>=2) {
                                    ResourceLocation loc = new ResourceLocation(nameSplit[0], nameSplit[1]);
                                    int meta;
                                    try {
                                        meta = nameSplit.length > 2 ? (nameSplit[2].equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(nameSplit[2])) : 0;
                                    } catch (Exception e) {
                                        meta = 0;
                                        RaidsLogger.logError("Entry" + name + " has a non integer, non wildcard metadata value", e);
                                    }
                                    if (ForgeRegistries.ITEMS.containsKey(loc)) {
                                        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(loc), 1, meta);
                                        if (nbt != null) stack.setTagCompound(nbt);
                                        RaidsLogger.logInfo("Loaded gift " + stack + " for career " + profession.getRegistryName() + " - " + career.getName());
                                        stacks.add(stack);
                                    }
                                } else {
                                    throw new Exception(name + " is not a valid registry");
                                }
                            }
                            if (!stacks.isEmpty()) {
                                RaidsLogger.logInfo("Loaded gifts for career " + profession.getRegistryName() + " - " + career.getName());
                                gifts.put(career, stacks);
                            }
                        } catch (Exception e) {
                            //RaidsLogger.logError("Failed loading villager gifts entry " + entry.getKey() + " " + e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    RaidsLogger.logError("Failed loading villager gifts entry " + entry.getKey() + " " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            RaidsLogger.logError("Failed to load villager gifts config " + e.getMessage(), e);
        }
    }
    
    public List<ItemStack> getPossibleGifts(VillagerRegistry.VillagerCareer career) {
        return gifts.containsKey(career) ? gifts.get(career) : Lists.newArrayList();
    }
    
    public List<ItemStack> getPossibleGifts(VillagerRegistry.VillagerProfession profession, int career) {
        List<VillagerRegistry.VillagerCareer> careers = ((IVillagerProfession) profession).getCareers();
        return career >= careers.size() ? Lists.newArrayList() : getPossibleGifts(careers.get(career));
    }
    
    public ItemStack getGift(EntityVillager villager) {
        if (villager.isChild()) return new ItemStack(Blocks.RED_FLOWER);
        List<ItemStack> stacks = getPossibleGifts(villager.getProfessionForge(), ((IVillager)villager).getCareer());
        return stacks.isEmpty() ? new ItemStack(Blocks.RED_FLOWER) : stacks.get(villager.getRNG().nextInt(stacks.size())).copy();
    }
    
}
