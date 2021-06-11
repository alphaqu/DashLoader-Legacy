package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

public class PairEntry<K, V> {
    @Serialize(order = 0)
    public final K key;
    @Serialize(order = 1)
    public final V value;

    public PairEntry(@Deserialize("key") K key,
                     @Deserialize("value") V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> PairEntry<K, V> of(K key, V value) {
        return new PairEntry<>(key, value);
    }


}
