package net.smileycorp.raids.common.event;

import net.smileycorp.raids.common.raid.Raid;

public class CustomRaidWaveStartEvent extends CustomRaidEvent {
    
    private int wave;
    
    public CustomRaidWaveStartEvent(Raid raid, int wave) {
        super(raid);
        this.wave = Math.max(1, wave);
    }
    
    public int getWave() {
        return wave;
    }
    
    public boolean setWave(int wave) {
        if (wave <= 0) return false;
        this.wave = wave;
        return true;
    }
    
}
