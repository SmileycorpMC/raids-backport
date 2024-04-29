package net.smileycorp.raids.mixin;

import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.smileycorp.raids.common.util.accessors.IVillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(VillagerRegistry.VillagerProfession.class)
public abstract class MixinVillagerProfession implements IVillagerProfession {
    
    @Shadow private List<VillagerRegistry.VillagerCareer> careers;
    
    @Override
    public List<VillagerRegistry.VillagerCareer> getCareers() {
        return careers;
    }
    
}
