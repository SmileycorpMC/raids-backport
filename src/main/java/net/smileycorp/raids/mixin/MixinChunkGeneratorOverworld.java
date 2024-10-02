package net.smileycorp.raids.mixin;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.smileycorp.raids.common.world.MapGenOutpost;
import net.smileycorp.raids.config.OutpostConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ChunkGeneratorOverworld.class)
public abstract class MixinChunkGeneratorOverworld implements IChunkGenerator {
    
    @Shadow @Final private World world;
    
    @Shadow @Final private boolean mapFeaturesEnabled;
    
    @Inject(method = "generateChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ChunkPrimer;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void raids$generateChunk(int x, int z, CallbackInfoReturnable<Chunk> callback, ChunkPrimer primer) {
        if (mapFeaturesEnabled) MapGenOutpost.getInstance(this).generate(world, x, z, primer);
    }
    
    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    public void raids$getPossibleCreatures(EnumCreatureType type, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> callback) {
        MapGenOutpost outposts = MapGenOutpost.getInstance(this);
        if (mapFeaturesEnabled && type == EnumCreatureType.MONSTER && outposts.isInsideStructure(pos)) {
            callback.setReturnValue(OutpostConfig.getSpawnEntities());
        }
    }
    
    @Inject(method = "recreateStructures", at = @At(value = "TAIL"))
    public void recreateStructures(Chunk chunkIn, int x, int z, CallbackInfo callback) {
        if (mapFeaturesEnabled) MapGenOutpost.getInstance(this).generate(world, x, z, null);
    }

}
