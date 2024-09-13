package net.smileycorp.raids.common;

import net.minecraft.util.SoundEvent;

public class RaidsSoundEvents {
    
    public static final SoundEvent RAID_HORN = registerSound("event.raid.horn");
    public static final SoundEvent PILLAGER_AMBIENT = registerSound("entity.pillager.ambient");
    public static final SoundEvent PILLAGER_HURT = registerSound("entity.pillager.hurt");
    public static final SoundEvent PILLAGER_DEATH = registerSound("entity.pillager.death");
    public static final SoundEvent PILLAGER_CELEBRATE = registerSound("entity.pillager.celebrate");
    public static final SoundEvent RAVAGER_AMBIENT = registerSound("entity.ravager.ambient");
    public static final SoundEvent RAVAGER_ATTACK = registerSound("entity.ravager.attack");
    public static final SoundEvent RAVAGER_CELEBRATE = registerSound("entity.ravager.celebrate");
    public static final SoundEvent RAVAGER_DEATH = registerSound("entity.ravager.death");
    public static final SoundEvent RAVAGER_HURT = registerSound("entity.ravager.hurt");
    public static final SoundEvent RAVAGER_STEP = registerSound("entity.ravager.step");
    public static final SoundEvent RAVAGER_STUNNED = registerSound("entity.ravager.stunned");
    public static final SoundEvent RAVAGER_ROAR = registerSound("entity.ravager.roar");
    public static final SoundEvent BAD_OMEN = registerSound("event.mob_effect.bad_omen");
    public static final SoundEvent RAID_OMEN = registerSound("event.mob_effect.raid_omen");
    public static final SoundEvent OMINOUS_BOTTLE_USE = registerSound("item.ominous_bottle.dispose");

    public static SoundEvent registerSound(String name) {
        SoundEvent newSound = new SoundEvent(Constants.loc(name));
        newSound.setRegistryName(name);
        return newSound;
    }

}
