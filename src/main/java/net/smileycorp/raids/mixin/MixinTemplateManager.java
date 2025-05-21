package net.smileycorp.raids.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.Raids;
import net.smileycorp.raids.common.util.RaidsLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Mixin(TemplateManager.class)
public abstract class MixinTemplateManager {

    @Shadow protected abstract void readTemplateFromStream(String path, InputStream stream) throws IOException;

    @Inject(at = @At("HEAD"), method = "readTemplateFromJar", cancellable = true)
    public void readTemplateFromJar(ResourceLocation location, CallbackInfoReturnable<Boolean> callback) {
        if (!location.getResourceDomain().equals(Constants.MODID)) return;
        if (!location.getResourcePath().contains("pillager_outpost")) return;
        File file = new File(Raids.CONFIG_FOLDER, location.getResourcePath() + ".nbt");
        try {
            readTemplateFromStream(location.getResourcePath(), Files.newInputStream(file.toPath()));
            callback.setReturnValue(true);
        } catch (Exception e) {
            RaidsLogger.logError("Failed reading structure " + file, e);
            callback.setReturnValue(false);
        }
    }

}
