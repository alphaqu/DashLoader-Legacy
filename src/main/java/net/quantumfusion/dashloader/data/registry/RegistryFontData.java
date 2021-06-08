package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMaps;
import net.quantumfusion.dashloader.font.DashFont;

import java.util.Map;

public class RegistryFontData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Int2ObjectSortedMap<DashFont> fonts;

    public RegistryFontData(@Deserialize("fonts") Int2ObjectSortedMap<DashFont> fonts) {
        this.fonts = fonts;
    }
}
