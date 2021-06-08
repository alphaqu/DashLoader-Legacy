package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Pntr2PntrMap {

    @Serialize(order = 0)
    public List<Pntr2PntrEntry> data;

    public Pntr2PntrMap(@Deserialize("data") List<Pntr2PntrEntry> data) {
        this.data = data;
    }

    public Pntr2PntrMap(int size) {
        data = new ArrayList<>(size);
    }

    public Pntr2PntrMap() {
        data = new ArrayList<>();
    }

    public void put(int key, int value) {
        data.add(new Pntr2PntrEntry(key, value));
    }

    public void forEach(Consumer<Pntr2PntrEntry> action) {
        data.forEach(action);
    }

    public int size() {
        return data.size();
    }
}
