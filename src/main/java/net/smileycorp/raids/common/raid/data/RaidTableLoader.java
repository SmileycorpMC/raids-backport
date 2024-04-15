package net.smileycorp.raids.common.raid.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

public class RaidTableLoader {
    
    public static RaidTableLoader INSTANCE;
    private final File directory;
    private final TreeSet<RaidSpawnTable> tables = Sets.newTreeSet(RaidSpawnTable::sort);
    
    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new RaidTableLoader(new File(event.getModConfigurationDirectory().getPath() + "/raids/raid_tables"));
    }
    
    private RaidTableLoader(File directory) {
        this.directory = directory;
    }
    
    public void loadTables() {
        JsonParser parser = new JsonParser();
        for (File file : directory.listFiles((f, s) -> s.endsWith(".json"))) {
            try {
                JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
                List<RaidEntry> entries = Lists.newArrayList();
                List<Condition> conditions = Lists.newArrayList();
                if (json.has("entries")) for (JsonElement element : json.get("entries").getAsJsonArray()) {
                    RaidEntry entry = RaidEntry.deserialize(element.getAsJsonObject());
                    if (entry != null) entries.add(entry);
                }
            } catch (Exception e) {
                RaidsLogger.logError("Failed loading raid table " + file.getName(), e);
            }
        }
    }

}
