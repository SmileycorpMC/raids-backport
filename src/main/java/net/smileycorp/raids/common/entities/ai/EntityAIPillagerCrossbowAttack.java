package net.smileycorp.raids.common.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.smileycorp.raids.common.entities.EntityPillager;
import net.smileycorp.raids.integration.ModIntegration;

public class EntityAIPillagerCrossbowAttack extends EntityAIBase {
    
    private final EntityPillager entity;
    private State state = State.UNCHARGED;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;
    
    public EntityAIPillagerCrossbowAttack(EntityPillager entity) {
        this.entity = entity;
        setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() != null && entity.getAttackTarget().isEntityAlive() &&
                ModIntegration.isCrossbow(entity.getHeldItemMainhand()) || ModIntegration.isCrossbow(entity.getHeldItemOffhand());
    }
    
    public void resetTask() {
        super.resetTask();
        entity.setAttackTarget(null);
        seeTime = 0;
        if (ModIntegration.isCrossbow(entity.getActiveItemStack())) {
            ModIntegration.setCharged(entity.getActiveItemStack(), false);
            entity.resetActiveHand();
            entity.setChargingCrossbow(false);
        }
    }
    
    @Override
    public void updateTask() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target != null) {
            boolean canSee = entity.getEntitySenses().canSee(target);
            boolean currentlySeeing = seeTime > 0;
            if (canSee != currentlySeeing) seeTime = 0;
            if (canSee) seeTime++;
            else seeTime--;
            double d0 = entity.getDistanceSq(target);
            boolean flag2 = (d0 > 400 || this.seeTime < 5) && attackDelay == 0;
            if (flag2) {
                if (updatePathDelay-- <= 0) {
                    entity.getNavigator().tryMoveToEntityLiving(target, state == State.UNCHARGED ? 1 : 0.5);
                    updatePathDelay = entity.getRNG().nextInt(20) + 20;
                }
            } else {
                this.updatePathDelay = 0;
                entity.getNavigator().clearPath();
            }
            entity.getLookHelper().setLookPositionWithEntity(target, 30, 30);
            if (state == State.UNCHARGED) {
                if (!flag2) {
                    for (EnumHand hand : EnumHand.values()) if (ModIntegration.isCrossbow(entity.getHeldItem(hand))) {
                        entity.setActiveHand(hand);
                        break;
                    }
                    state = State.CHARGING;
                    entity.setChargingCrossbow(true);
                }
            } else if (state == State.CHARGING) {
                if (!entity.isHandActive()) state = State.UNCHARGED;
                int i = entity.getItemInUseCount();
                ItemStack stack = entity.getActiveItemStack();
                if (ModIntegration.isCrossbow(stack) && i < -stack.getMaxItemUseDuration()) {
                    entity.resetActiveHand();
                    state = State.CHARGED;
                    stack.onPlayerStoppedUsing(entity.world, entity, 0);
                    attackDelay = 20 + entity.getRNG().nextInt(20);
                    entity.setChargingCrossbow(false);
                }
            } else if (state == State.CHARGED) {
                if (attackDelay-- == 0) state = State.READY_TO_ATTACK;
            } else if (state == State.READY_TO_ATTACK && canSee) {
                performCrossbowAttack(1);
                ItemStack stack = ItemStack.EMPTY;
                for (EnumHand hand : EnumHand.values()) if (ModIntegration.isCrossbow(entity.getHeldItem(hand))) {
                    stack = entity.getHeldItem(hand);
                    break;
                }
                ModIntegration.setCharged(stack, false);
                state = State.UNCHARGED;
            }
        }
    }
    
    protected void performCrossbowAttack(float distance) {
        ItemStack stack = ItemStack.EMPTY;
        for (EnumHand hand : EnumHand.values()) if (ModIntegration.isCrossbow(entity.getHeldItem(hand))) {
            stack = entity.getHeldItem(hand);
            break;
        }
        if (stack.isEmpty()) return;
        ModIntegration.performShooting(entity, stack, distance);
        entity.onCrossbowAttackPerformed();
    }
    
    enum State {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }
    
}
