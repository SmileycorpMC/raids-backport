package net.smileycorp.raids.common.util.accessors;

import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;

public interface IVillagerProfession {
    
    List<VillagerRegistry.VillagerCareer> getCareers();
    
}
