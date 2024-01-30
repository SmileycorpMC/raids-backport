package net.smileycorp.raids.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;

public interface IFireworksProjectile extends IProjectile {

    void setOwner(Entity owner);

    Entity getOwner();

    boolean hasOwner();

    boolean isShotAtAngle();

    void setShotAtAngle();

}
