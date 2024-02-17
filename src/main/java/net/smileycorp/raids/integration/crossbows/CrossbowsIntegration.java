package net.smileycorp.raids.integration.crossbows;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.ai.EntityAIAttackRangedCrossbow;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.common.interfaces.ICrossbowArrow;

public class CrossbowsIntegration {
    
    public static void addTask(EntityPillager entity) {
        entity.tasks.addTask(3, new EntityAIAttackRangedCrossbow<EntityPillager>(entity, 1.0D, 20,
                entity::setChargingCrossbow, entity::onCrossbowAttackPerformed));
    }
    
    public static ItemStack getCrossbow() {
        return new ItemStack(CrossbowsContent.CROSSBOW);
    }
    
    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() == CrossbowsContent.CROSSBOW;
    }
    
    public static boolean isCrossbowProjectile(Entity entity) {
        return entity instanceof ICrossbowArrow && ((ICrossbowArrow)entity).shotFromCrossbow();
    }
    
}
