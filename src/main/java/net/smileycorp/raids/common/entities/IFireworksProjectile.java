package net.smileycorp.raids.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;

public interface IFireworksProjectile extends IProjectile {

    void setOwner(Entity owner);

    Entity getOwner();

    boolean hasOwner();

    boolean isShotAtAngle();

    void setShotAtAngle();

}
