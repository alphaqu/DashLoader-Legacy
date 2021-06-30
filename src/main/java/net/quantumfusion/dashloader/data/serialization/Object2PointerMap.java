package net.quantumfusion.dashloader.data.serialization;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Object2PointerMap<O> {


    @Serialize(order = 0)
    public final List<Entry<O>> data;

    public Object2PointerMap(@Deserialize("data") List<Entry<O>> data) {
        this.data = data;
    }

    public Object2PointerMap(int size) {
        data = new ArrayList<>(size);
    }

    public Object2PointerMap(Map<O, Integer> map) {
        data = new ArrayList<>(map.size());
        map.forEach((o, integer) -> data.add(Entry.of(o, integer)));
    }

    public Object2PointerMap() {
        data = new ArrayList<>();
    }

    public void put(O key, int value) {
        data.add(Entry.of(key, value));
    }

    public void forEach(Consumer<Entry<O>> action) {
        data.forEach(action);
    }

    public int size() {
        return data.size();
    }

    public Map<O, Integer> convert() {
        Map<O, Integer> map = new HashMap<>((int) (data.size() / 0.75));
        data.forEach(entry -> map.put(entry.key, entry.value));
        return map;
    }

    public static class Entry<O> {
        @Serialize(order = 0)
        public final O key;
        @Serialize(order = 1)
        public final int value;

        public Entry(@Deserialize("key") O key,
                     @Deserialize("value") int value) {
            this.key = key;
            this.value = value;
        }

        public static <O> Entry<O> of(O key, int value) {
            return new Entry<>(key, value);
        }

    }
}
