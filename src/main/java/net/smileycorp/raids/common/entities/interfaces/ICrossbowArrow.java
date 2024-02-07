package net.smileycorp.raids.common.entities.interfaces;

public interface ICrossbowArrow {

    void setShotFromCrossbow(boolean crossbow);

    void setPierceLevel(byte level);

    boolean shotFromCrossbow();

    byte getPierceLevel();

}
