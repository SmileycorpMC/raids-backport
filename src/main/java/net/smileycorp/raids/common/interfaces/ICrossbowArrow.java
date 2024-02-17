package net.smileycorp.raids.common.interfaces;

public interface ICrossbowArrow {

    void setShotFromCrossbow(boolean crossbow);

    void setPierceLevel(byte level);

    boolean shotFromCrossbow();

    byte getPierceLevel();

}
