package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord") // it clearly fucking cant intelj, the variables wont be public
public final class Pntr2PntrEntry {
    @Serialize(order = 0)
    public final int key;
    @Serialize(order = 1)
    public final int value;

    public Pntr2PntrEntry(@Deserialize("key") int key,
                          @Deserialize("value") int value) {
        this.key = key;
        this.value = value;
    }

    public int key() {
        return key;
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pntr2PntrEntry) obj;
        return this.key == that.key &&
                this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Pntr2PntrEntry[" +
                "key=" + key + ", " +
                "value=" + value + ']';
    }

}
