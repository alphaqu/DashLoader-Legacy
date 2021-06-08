package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

public record Pntr2ObjectEntry<O>(@Serialize(order = 0) int key, @Serialize(order = 1) O value) {
    public Pntr2ObjectEntry(@Deserialize("key") int key,
                            @Deserialize("value") O value) {
        this.key = key;
        this.value = value;
    }
}
