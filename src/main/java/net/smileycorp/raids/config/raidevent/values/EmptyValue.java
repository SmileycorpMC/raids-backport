package net.smileycorp.raids.config.raidevent.values;

import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.raids.common.raid.RaidContext;

public class EmptyValue<T extends Comparable<T>> implements Value<T> {
    
    private final DataType<T> type;
    
    public EmptyValue(DataType<T> type) {
        this.type = type;
    }
    
    @Override
    public T get(RaidContext ctx) {
        return type.getDefaultValue();
    }
    
}
