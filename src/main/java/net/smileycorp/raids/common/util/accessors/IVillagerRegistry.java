package net.smileycorp.raids.common.util.accessors;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public interface IVillagerRegistry {
    
    VillagerRegistry.VillagerProfession getProfession(ResourceLocation location);
    
}
