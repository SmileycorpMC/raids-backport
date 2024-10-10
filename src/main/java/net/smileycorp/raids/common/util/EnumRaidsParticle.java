package net.smileycorp.raids.common.util;

import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum EnumRaidsParticle {
    
    RAID_OMEN((buf, data) -> buf.writeInt((int)(double)data[0]), (buf -> new Double[] {(double) buf.readInt()})),
    VIBRATION((buf, data) -> {buf.writeDouble(data[0]); buf.writeDouble(data[1]); buf.writeDouble(data[2]);},
            (buf -> new Double[] {buf.readDouble(), buf.readDouble(), buf.readDouble()}));
    
    private final BiConsumer<ByteBuf, Double[]> writeFunc;
    private final Function<ByteBuf, Double[]> readFunc;
    
    EnumRaidsParticle(BiConsumer<ByteBuf, Double[]> writeFunc, Function<ByteBuf, Double[]> readFunc) {
        this.writeFunc = writeFunc;
        this.readFunc = readFunc;
    }
    
    public void writeBytes(ByteBuf buf, Double[] data) {
        writeFunc.accept(buf, data);
    }
    
    public Double[] readBytes(ByteBuf buf) {
        return readFunc.apply(buf);
    }
    
}
