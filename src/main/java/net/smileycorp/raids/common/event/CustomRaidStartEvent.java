package net.smileycorp.raids.common.event;

import net.smileycorp.raids.common.raid.Raid;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class CustomRaidStartEvent extends CustomRaidEvent {
    
    public CustomRaidStartEvent(Raid raid) {
        super(raid);
    }
    
}
