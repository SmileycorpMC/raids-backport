package net.smileycorp.raids.config.raidevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.conditions.TableCondition;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class RaidTableLoader {
    
    public static RaidTableLoader INSTANCE;
    private final File directory;
    private final TreeSet<RaidSpawnTable> tables = Sets.newTreeSet(RaidSpawnTable::sort);
    private RaidSpawnTable default_table;
    
    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new RaidTableLoader(new File(event.getModConfigurationDirectory().getPath() + "/raids/raid_tables"));
    }
    
    private RaidTableLoader(File directory) {
        this.directory = directory;
    }
    
    public void generateDefaultData() {
        ArrayList<RaidEntry> entries = Lists.newArrayList();
        try {
            entries.add(new RaidEntry(RaidsContent.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}, null, null));
            entries.add(new RaidEntry(new ResourceLocation("vindication_illager"), new int[]{0, 0, 2, 0, 1, 4, 2, 5}, null, (difficulty, rand, raid, wave, isBonusWave) ->
                    difficulty == EnumDifficulty.EASY ? rand.nextInt(2) : difficulty == EnumDifficulty.NORMAL ? 1 : 2));
            entries.add(new RaidEntry(RaidsContent.RAVAGER, new int[]{0, 0, 1, 0, 0, 0, 0}, (raid, world, numSpawned) -> {
                int i = raid.getGroupsSpawned() + 1;
                if (i == raid.getNumGroups(EnumDifficulty.NORMAL)) return new EntityPillager(world);
                if (i >= raid.getNumGroups(EnumDifficulty.HARD))
                    return numSpawned.containsKey(EntityEvoker.class) ? new EntityVindicator(world) : new EntityEvoker(world);
                return null;
            }, (difficulty, rand, raid, wave, isBonusWave) -> difficulty != EnumDifficulty.EASY && isBonusWave ? 1 : 0));
            entries.add(new RaidEntry(new ResourceLocation("witch"), new int[]{0, 0, 0, 0, 3, 0, 0, 1}, null, (difficulty, rand, raid, wave, isBonusWave) ->
                    (difficulty == EnumDifficulty.EASY || wave <= 2 || wave == 4) ? 0 : 1));
            entries.add(new RaidEntry(new ResourceLocation("evocation_illager"), new int[]{0, 0, 0, 1, 0, 1, 0, 2}, null, null));
        } catch (Exception e) {
            RaidsLogger.logError("Failed adding default entries", e);
        }
        default_table = new RaidSpawnTable("default", entries, Lists.newArrayList(), RaidsSoundEvents.RAID_HORN);
        if (!directory.exists()) {
            RaidsLogger.logInfo("Raid table folder does not exist, generating default data");
            directory.mkdirs();
            File file = new File(directory, "default.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(gson.toJson(default_table.toJson()));
                }
            } catch (Exception e) {
                RaidsLogger.logError("Failed generating default raid table", e);
            }
        }
    }
    
    public void loadTables() {
        JsonParser parser = new JsonParser();
        for (File file : directory.listFiles((f, s) -> s.endsWith(".json"))) {
            try {
                JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
                List<RaidEntry> entries = Lists.newArrayList();
                List<TableCondition> conditions = Lists.newArrayList();
                if (json.has("entries")) for (JsonElement element : json.get("entries").getAsJsonArray()) {
                    RaidEntry entry = RaidEntry.deserialize(element.getAsJsonObject());
                    if (entry != null) entries.add(entry);
                }
            } catch (Exception e) {
                RaidsLogger.logError("Failed loading raid table " + file.getName(), e);
            }
        }
    }
    
    public RaidSpawnTable getSpawnTable(String name) {
        for (RaidSpawnTable table : tables) if (table.getName().equals(name)) return table;
        return null;
    }
    
    public RaidSpawnTable getSpawnTable(World world, BlockPos pos, EntityPlayer player, Random rand) {
        for (RaidSpawnTable table : tables) if (table.shouldApply(world, pos, player, rand)) return table;
        RaidsLogger.logError("No tables could be applied, using default table.", new NullPointerException());
        return null;
    }
    
    public TreeSet<RaidSpawnTable> spawnTables() {
        return tables;
    }
    
}
