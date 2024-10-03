package net.smileycorp.raids.mixin;

import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.entities.EntityAllay;
import net.smileycorp.raids.config.EntityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WoodlandMansionPieces.MansionTemplate.class)
public class MixinMansionTemplate {
    
    @Inject(at = @At("HEAD"), method = "handleDataMarker", cancellable = true)
    public void raids$handleDataMarker(String function, BlockPos pos, World world, Random rand, StructureBoundingBox sbb, CallbackInfo callback) {
        if (function.equals("Group of Allays")) {
            for (int i = 0; i < world.rand.nextInt(3) + 1; i++) {
                EntityAllay allay = new EntityAllay(world);
                allay.enablePersistence();
                allay.moveToBlockPosAndAngles(pos, 0, 0);
                world.spawnEntity(allay);
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            callback.cancel();
        }
    }
    
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/template/TemplateManager;getTemplate(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/world/gen/structure/template/Template;"), method = "loadTemplate")
    public Template raids$loadTemplate$getTemplate(TemplateManager instance, MinecraftServer server, ResourceLocation loc) {
       return instance.getTemplate(server, EntityConfig.mansionAllays && loc.getResourcePath().equals("mansion/2x2_a1") ? Constants.loc(loc.getResourcePath()) : loc);
    }

}
