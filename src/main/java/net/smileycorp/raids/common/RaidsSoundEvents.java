package net.smileycorp.raids.common;

import net.minecraft.util.SoundEvent;

public class RaidsSoundEvents {
    
    public static final SoundEvent RAID_HORN = register("event.raid.horn");
    public static final SoundEvent PILLAGER_AMBIENT = register("entity.pillager.ambient");
    public static final SoundEvent PILLAGER_HURT = register("entity.pillager.hurt");
    public static final SoundEvent PILLAGER_DEATH = register("entity.pillager.death");
    public static final SoundEvent PILLAGER_CELEBRATE = register("entity.pillager.celebrate");
    public static final SoundEvent RAVAGER_AMBIENT = register("entity.ravager.ambient");
    public static final SoundEvent RAVAGER_ATTACK = register("entity.ravager.attack");
    public static final SoundEvent RAVAGER_CELEBRATE = register("entity.ravager.celebrate");
    public static final SoundEvent RAVAGER_DEATH = register("entity.ravager.death");
    public static final SoundEvent RAVAGER_HURT = register("entity.ravager.hurt");
    public static final SoundEvent RAVAGER_STEP = register("entity.ravager.step");
    public static final SoundEvent RAVAGER_STUNNED = register("entity.ravager.stunned");
    public static final SoundEvent RAVAGER_ROAR = register("entity.ravager.roar");
    public static final SoundEvent BAD_OMEN = register("event.mob_effect.bad_omen");
    public static final SoundEvent RAID_OMEN = register("event.mob_effect.raid_omen");
    public static final SoundEvent OMINOUS_BOTTLE_USE = register("item.ominous_bottle.dispose");
    public static final SoundEvent ALLAY_AMBIENT_WITH_ITEM = register("entity.allay.ambient_with_item");
    public static final SoundEvent ALLAY_AMBIENT_WITHOUT_ITEM = register("entity.allay.ambient_without_item");
    public static final SoundEvent ALLAY_DEATH = register("entity.allay.death");
    public static final SoundEvent ALLAY_HURT = register("entity.allay.hurt");
    public static final SoundEvent ALLAY_ITEM_GIVEN = register("entity.allay.item_given");
    public static final SoundEvent ALLAY_ITEM_TAKEN = register("entity.allay.item_taken");
    public static final SoundEvent ALLAY_THROW = register("entity.allay.item_thrown");

    public static SoundEvent register(String name) {
        SoundEvent newSound = new SoundEvent(Constants.loc(name));
        newSound.setRegistryName(name);
        return newSound;
    }

}
