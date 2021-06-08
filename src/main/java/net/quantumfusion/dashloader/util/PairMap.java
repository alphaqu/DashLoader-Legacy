package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PairMap<K, V> {

    @Serialize(order = 0)
    public List<PairEntry<K, V>> data;

    public PairMap(@Deserialize("data") List<PairEntry<K, V>> data) {
        this.data = data;
    }

    public PairMap(int size) {
        data = new ArrayList<>(size);
    }

    public PairMap() {
        data = new ArrayList<>();
    }

    public void put(K key, V value) {
        data.add(new PairEntry<>(key, value));
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        data.forEach(kvPairEntry -> action.accept(kvPairEntry.key(), kvPairEntry.value()));
    }

    public int size() {
        return data.size();
    }
}
