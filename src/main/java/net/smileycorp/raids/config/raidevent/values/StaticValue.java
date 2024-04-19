package net.smileycorp.raids.config.raidevent.values;

import net.smileycorp.raids.common.raid.RaidContext;

public class StaticValue<T extends Comparable<T>> implements Value<T> {
    
    private final T value;
    
    public StaticValue(T value) {
        this.value = value;
    }
    
    @Override
    public T get(RaidContext ctx) {
        return value;
    }
    
}
