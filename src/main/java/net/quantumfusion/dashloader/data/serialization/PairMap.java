package net.quantumfusion.dashloader.data.serialization;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PairMap<K, V> {

    @Serialize(order = 0)
    public List<Entry<K, V>> data;

    public PairMap(@Deserialize("data") List<Entry<K, V>> data) {
        this.data = data;
    }


    public PairMap(int size) {
        data = new ArrayList<>(size);
    }

    public PairMap() {
        data = new ArrayList<>();
    }

    public void put(K key, V value) {
        data.add(Entry.of(key, value));
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        data.forEach(kvPairEntry -> action.accept(kvPairEntry.key, kvPairEntry.value));
    }

    public int size() {
        return data.size();
    }


    public static class Entry<K, V> {
        @Serialize(order = 0)
        public final K key;
        @Serialize(order = 1)
        public final V value;

        public Entry(@Deserialize("key") K key,
                     @Deserialize("value") V value) {
            this.key = key;
            this.value = value;
        }

        public static <K, V> Entry<K, V> of(K key, V value) {
            return new Entry<>(key, value);
        }

    }
}
