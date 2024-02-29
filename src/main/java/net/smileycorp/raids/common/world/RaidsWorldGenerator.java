package net.smileycorp.raids.common.world;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RaidsWorldGenerator {
    
    @SubscribeEvent
    public void generate(PopulateChunkEvent.Post event) {
        if (!(event.getGenerator() instanceof ChunkGeneratorOverworld) || event.isHasVillageGenerated()) return;
        //((ChunkGeneratorOverworld)event.getGenerator()).mapFeaturesEnabled;
        MapGenOutpost.getInstance(event.getGenerator()).generateStructure(event.getWorld(), event.getRand(), new ChunkPos(event.getChunkX(), event.getChunkZ()));
    }
    
}
