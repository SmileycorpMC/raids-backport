package net.smileycorp.raids.mixin;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.smileycorp.raids.common.world.MapGenOutpost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkGeneratorFlat.class)
public abstract class MixinChunkGeneratorFlat implements IChunkGenerator {
    
    @Shadow @Final private World world;
    
    @Shadow @Final private boolean hasDecoration;
    
    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    public void getPossibleCreatures(EnumCreatureType type, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> callback) {
        MapGenOutpost outposts = MapGenOutpost.getInstance(this);
        if (hasDecoration && type == EnumCreatureType.MONSTER && outposts.isInsideStructure(pos)) callback.setReturnValue(outposts.getSpawnList());
    }
    
    @Inject(method = "recreateStructures", at = @At(value = "TAIL"))
    public void recreateStructures(Chunk chunkIn, int x, int z, CallbackInfo callback) {
        if (hasDecoration) MapGenOutpost.getInstance(this).generate(world, x, z, null);
    }

}
