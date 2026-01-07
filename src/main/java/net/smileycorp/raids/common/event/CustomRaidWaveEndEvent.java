package net.smileycorp.raids.common.event;

import net.smileycorp.raids.common.raid.Raid;

public class CustomRaidWaveEndEvent extends CustomRaidEvent {
    
    private final int wave;
    
    public CustomRaidWaveEndEvent(Raid raid, int wave) {
        super(raid);
        this.wave = wave;
    }
    
    public int getWave() {
        return wave;
    }
    
}
