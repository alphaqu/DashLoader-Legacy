package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

@SuppressWarnings("ClassCanBeRecord") // it clearly fucking cant intelj, the variables wont be public
public class Pntr2ObjectEntry<O> {
    @Serialize(order = 0)
    public final int key;
    @Serialize(order = 1)
    public final O value;


    public Pntr2ObjectEntry(@Deserialize("key") int key,
                            @Deserialize("value") O value) {
        this.key = key;
        this.value = value;
    }


    public int key() {
        return key;
    }

    public O value() {
        return value;
    }

}


