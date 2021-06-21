package net.quantumfusion.dashloader.util.serialization;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Pointer2ObjectMap<O> {
    @Serialize(order = 0)
    public List<Entry<O>> data;

    public Pointer2ObjectMap(@Deserialize("data") List<Entry<O>> data) {
        this.data = data;
    }

    public Pointer2ObjectMap(int size) {
        data = new ArrayList<>(size);
    }

    public Pointer2ObjectMap(Map<Integer, O> map) {
        data = new ArrayList<>(map.size());
        map.forEach((integer, o) -> data.add(Entry.of(integer, o)));
    }

    public Pointer2ObjectMap() {
        data = new ArrayList<>();
    }

    public void put(int key, O value) {
        data.add(Entry.of(key, value));
    }

    public void forEach(Consumer<Entry<O>> action) {
        data.forEach(action);
    }

    public int size() {
        return data.size();
    }

    public Int2ObjectMap<O> convert() {
        Int2ObjectOpenHashMap<O> map = new Int2ObjectOpenHashMap<>((int) (data.size() / 0.75));
        data.forEach(entry -> map.put(entry.key, entry.value));
        return map;
    }

    public static class Entry<O> {
        @Serialize(order = 0)
        public final int key;
        @Serialize(order = 1)
        public final O value;

        public Entry(@Deserialize("key") int key,
                     @Deserialize("value") O value) {
            this.key = key;
            this.value = value;
        }

        public static <O> Entry<O> of(int key, O value) {
            return new Entry<>(key, value);
        }

    }
}
