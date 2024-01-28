package net.smileycorp.raids;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.SoundEvent;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.entities.ICrossbowArrow;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityArrow.class)
public class MixinEntityArrow implements ICrossbowArrow {

    private boolean shotFromCrossbow;


    @Override
    public void setShotFromCrossbow(boolean crossbow) {

    }

    @Override
    public void setSoundEvent(SoundEvent sound) {

    }

    @Override
    public void setPierceLevel(byte level) {

    }

    @Override
    public boolean shotFromCrossbow() {
        return false;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return null;
    }

    @Override
    public byte getPierceLevel() {
        return 0;
    }
}
