package net.smileycorp.raids.mixin;

import net.minecraft.block.BlockJukebox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.smileycorp.raids.common.entities.EntityAllay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntity.class)
public abstract class MixinTileEntity {
    
    @Shadow
    protected World world;
    
    @Inject(at = @At("TAIL"), method = "setWorld")
    public void raids$init(World world, CallbackInfo callback) {
        if (world.isRemote) return;
        if (!((Object)this instanceof BlockJukebox.TileEntityJukebox)) return;
        EntityAllay.JUKEBOXES.add((BlockJukebox.TileEntityJukebox)(Object)this);
    }
    
    @Inject(at = @At("HEAD"), method = "invalidate")
    public void raids$invalidate(CallbackInfo callback) {
        if (world.isRemote) return;
        if (!((Object)this instanceof BlockJukebox.TileEntityJukebox)) return;
        EntityAllay.JUKEBOXES.remove(this);
    }
    
}
