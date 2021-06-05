package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

public record PairEntry<K, V>(@Serialize(order = 0) K key, @Serialize(order = 1) V value) {
    public PairEntry(@Deserialize("key") K key,
                     @Deserialize("value") V value) {
        this.key = key;
        this.value = value;
    }
}
