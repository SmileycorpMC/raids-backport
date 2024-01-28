package net.smileycorp.raids.common.entities;

import net.minecraft.util.SoundEvent;

public interface ICrossbowArrow {

    void setShotFromCrossbow(boolean crossbow);

    void setSoundEvent(SoundEvent sound);

    void setPierceLevel(byte level);

    boolean shotFromCrossbow();

    SoundEvent getSoundEvent();

    byte getPierceLevel();

}
