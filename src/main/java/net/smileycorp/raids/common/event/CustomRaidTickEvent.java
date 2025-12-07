package net.smileycorp.raids.common.event;

import net.smileycorp.raids.common.raid.Raid;

public class CustomRaidTickEvent extends CustomRaidEvent {
    
    private final int wave;
    private Boolean endState;
    
    public CustomRaidTickEvent(Raid raid, int wave) {
        super(raid);
        this.wave = Math.max(1, wave);
    }
    
    public int getWave() {
        return wave;
    }
    
    public void setEnd(boolean win) {
        endState = win;
    }
    
    public boolean shouldEndRaid() {
        return endState != null;
    }
    
    public boolean endAsWin() {
        return Boolean.TRUE.equals(endState);
    }
    
}
