package net.smileycorp.raids.common.event;

import net.smileycorp.raids.common.raid.Raid;

public class CustomRaidEndEvent extends CustomRaidEvent {
    
    private final boolean win;
    
    public CustomRaidEndEvent(Raid raid, boolean win) {
        super(raid);
        this.win = win;
    }
    
    public boolean isWin() {
        return win;
    }
    
}
