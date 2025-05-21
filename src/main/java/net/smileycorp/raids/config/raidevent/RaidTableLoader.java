package net.smileycorp.raids.config.raidevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.raid.Raid;
import net.smileycorp.raids.common.raid.RaidContext;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.config.raidevent.conditions.ConditionRegistry;
import net.smileycorp.raids.config.raidevent.conditions.RaidCondition;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.util.List;
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
        if (!directory.exists()) {
            RaidsLogger.logInfo("Raid table folder does not exist, generating default data");
            directory.mkdirs();
            try {
                FileUtils.copyInputStreamToFile(RaidTableLoader.class.getResourceAsStream("/config-defaults/raid_tables/default.json"),
                        new File(directory, "default.json"));
                FileUtils.copyInputStreamToFile(RaidTableLoader.class.getResourceAsStream("/config-defaults/raid_tables/advanced.json"),
                        new File(directory, "advanced.json"));
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
                List<RaidCondition> conditions = Lists.newArrayList();
                SoundEvent sound = null;
                if (json.has("entries")) for (JsonElement element : json.get("entries").getAsJsonArray()) {
                    RaidEntry entry = RaidEntry.deserialize(element.getAsJsonObject());
                    if (entry != null) entries.add(entry);
                }
                if (json.has("conditions")) for (JsonElement element : json.get("conditions").getAsJsonArray()) {
                    RaidCondition condition = ConditionRegistry.INSTANCE.readCondition(element.getAsJsonObject());
                    if (condition != null) conditions.add(condition);
                }
                if (json.has("sound")) {
                    try {
                        sound = new SoundEvent(new ResourceLocation(json.get("sound").getAsString()));
                    } catch (Exception e) {
                        RaidsLogger.logError("Failed reading sound " + json.get("sound") + " from table " + file.getName() + ", using default", e);
                    }
                }
                tables.add(new RaidSpawnTable(file.getName(), entries, conditions, sound));
                RaidsLogger.logInfo("Loaded raid table " + file.getName());
            } catch (Exception e) {
                RaidsLogger.logError("Failed loading raid table " + file.getName(), e);
            }
        }
    }
    
    public RaidSpawnTable getSpawnTable(String name) {
        for (RaidSpawnTable table : tables) if (table.getName().equals(name)) return table;
        return null;
    }
    
    public RaidSpawnTable getSpawnTable(RaidContext ctx) {
        for (RaidSpawnTable table : tables) if (table.shouldApply(ctx)) return table;
        RaidsLogger.logError("No tables could be applied, cancelling raid", new NullPointerException());
        return null;
    }
    
    public TreeSet<RaidSpawnTable> spawnTables() {
        return tables;
    }
    
    public RaidSpawnTable getDefaultTable() {
        if (default_table != null) return default_table;
        List<RaidEntry> entries = Lists.newArrayList();
        try {
            entries.add(new RaidEntry(Constants.loc("pillager"), null, new int[]{4, 3, 3, 4, 4, 4, 2}, null, null));
            
            entries.add(new RaidEntry(new ResourceLocation("vindication_illager"), null, new int[]{0, 0, 2, 0, 1, 4, 2, 5}, null, ctx ->
                    ctx.getDifficulty() == EnumDifficulty.EASY ? ctx.getRand().nextInt(2) : ctx.getDifficulty() == EnumDifficulty.NORMAL ? 1 : 2));
            
            entries.add(new RaidEntry(Constants.loc("ravager"), null, new int[]{0, 1, 0, 0, 0, 0, 2}, ctx -> {
                Raid raid = ctx.getRaid();
                int i = raid.getGroupsSpawned() + 1;
                if (i == raid.getNumGroups(EnumDifficulty.NORMAL)) return Constants.locStr("pillager");
                if (i >= raid.getNumGroups(EnumDifficulty.HARD))
                    return ctx.getNumSpawned(EntityEvoker.class) > 0 ? "vindication_illager" : "evocation_illager";
                return null;
            }, ctx -> ctx.getDifficulty() != EnumDifficulty.EASY && ctx.isBonusWave() ? 1 : 0));
            
            entries.add(new RaidEntry(new ResourceLocation("witch"), null, new int[]{0, 0, 0, 3, 0, 0, 1}, null, ctx ->
                    (ctx.getDifficulty() == EnumDifficulty.EASY || ctx.getWave() <= 2 || ctx.getWave() == 4) ? 0 : 1));
            
            entries.add(new RaidEntry(new ResourceLocation("evocation_illager"), null, new int[]{0, 0, 1, 0, 1, 0, 2}, null, null));
        } catch (Exception e) {
            RaidsLogger.logError("Failed adding default entries", e);
        }
        default_table = new RaidSpawnTable("default", entries, Lists.newArrayList(), RaidsSoundEvents.RAID_HORN);
        return default_table;
    }
    
}
