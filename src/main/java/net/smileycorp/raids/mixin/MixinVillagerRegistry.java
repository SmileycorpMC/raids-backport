package net.smileycorp.raids.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.smileycorp.raids.common.util.accessors.IVillagerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerRegistry.class)
public class MixinVillagerRegistry implements IVillagerRegistry {
    
    @Shadow private RegistryNamespaced<ResourceLocation, VillagerRegistry.VillagerProfession> REGISTRY;
    
    @Override
    public VillagerRegistry.VillagerProfession getProfession(ResourceLocation location) {
        return REGISTRY.containsKey(location) ? REGISTRY.getObject(location) : null;
    }
    
}
