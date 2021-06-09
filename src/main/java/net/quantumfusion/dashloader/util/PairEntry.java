package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.Objects;

public final class PairEntry<K, V> {
    @Serialize(order = 0)
    public final K key;
    @Serialize(order = 1)
    public final V value;

    public PairEntry(@Deserialize("key") K key,
                     @Deserialize("value") V value) {
        this.key = key;
        this.value = value;
    }

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PairEntry) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "PairEntry[" +
                "key=" + key + ", " +
                "value=" + value + ']';
    }

}
