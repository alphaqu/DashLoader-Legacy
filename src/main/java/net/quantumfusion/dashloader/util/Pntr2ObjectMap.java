package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Pntr2ObjectMap<O> {

    @Serialize(order = 0)
    public List<Pntr2ObjectEntry<O>> data;

    public Pntr2ObjectMap(@Deserialize("data") List<Pntr2ObjectEntry<O>> data) {
        this.data = data;
    }

    public Pntr2ObjectMap(int size) {
        data = new ArrayList<>(size);
    }

    public Pntr2ObjectMap(Int2ObjectMap<O> map) {
        data = new ArrayList<>(map.size());
        map.forEach((integer, o) -> data.add(new Pntr2ObjectEntry<>(integer, o)));
    }

    public Pntr2ObjectMap() {
        data = new ArrayList<>();
    }

    public void put(int key, O value) {
        data.add(new Pntr2ObjectEntry<>(key, value));
    }

    public void forEach(Consumer<Pntr2ObjectEntry<O>> action) {
        data.forEach(action);
    }

    public int size() {
        return data.size();
    }


    public Int2ObjectMap<O> convert() {
        Int2ObjectOpenHashMap<O> out = new Int2ObjectOpenHashMap<>();
        forEach(entry -> out.put(entry.key(), entry.value()));
        return out;
    }
}
