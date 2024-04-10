package net.smileycorp.raids.mixin;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.smileycorp.raids.common.util.ILootPool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

//access transformer keeps breaking
@Mixin(LootPool.class)
public class MixinLootPool implements ILootPool {
    
    @Shadow @Final private List<LootEntry> lootEntries;
    
    @Override
    public boolean isEmpty() {
        return lootEntries.isEmpty();
    }
    
}
