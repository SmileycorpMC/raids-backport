package net.smileycorp.raids.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.smileycorp.raids.common.util.RaidsCriterionTrigger;

public class RaidsAdvancements {

    public static final RaidsCriterionTrigger WHOS_THE_PILLAGER = new RaidsCriterionTrigger("whos_the_pillager");
    public static final RaidsCriterionTrigger VOLUNTARY_EXILE = new RaidsCriterionTrigger("voluntary_exile");
    public static final RaidsCriterionTrigger RAID_VICTORY = new RaidsCriterionTrigger("raid_victory");
    public static final RaidsCriterionTrigger OVERLEVELED = new RaidsCriterionTrigger("overleveled");
    public static final RaidsCriterionTrigger ALLAY_DELIVERS_ITEM = new RaidsCriterionTrigger("allay_delivers_item");
    public static final RaidsCriterionTrigger BIRTHDAY_SONG = new RaidsCriterionTrigger("birthday_song");
    public static final RaidsCriterionTrigger FRIEND_INSIDE_ME = new RaidsCriterionTrigger("friend_inside_me");

    public static void register() {
        CriteriaTriggers.register(WHOS_THE_PILLAGER);
        CriteriaTriggers.register(VOLUNTARY_EXILE);
        CriteriaTriggers.register(RAID_VICTORY);
        CriteriaTriggers.register(OVERLEVELED);
        CriteriaTriggers.register(ALLAY_DELIVERS_ITEM);
        CriteriaTriggers.register(BIRTHDAY_SONG);
        CriteriaTriggers.register(FRIEND_INSIDE_ME);
    }

}
