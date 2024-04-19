package net.smileycorp.raids.config.raidevent.conditions;

import net.smileycorp.raids.common.raid.RaidContext;

public interface RaidCondition {
    
    boolean apply(RaidContext ctx);
    
}
