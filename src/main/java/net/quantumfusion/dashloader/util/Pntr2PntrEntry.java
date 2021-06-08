package net.quantumfusion.dashloader.util;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

public record Pntr2PntrEntry(@Serialize(order = 0) int key, @Serialize(order = 1) int value) {
    public Pntr2PntrEntry(@Deserialize("key") int key,
                          @Deserialize("value") int value) {
        this.key = key;
        this.value = value;
    }
}
