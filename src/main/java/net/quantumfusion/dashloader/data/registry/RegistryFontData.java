package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.font.DashFont;

import java.util.Map;

public class RegistryFontData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Map<Integer, DashFont> fonts;

    public RegistryFontData(@Deserialize("fonts") Map<Integer, DashFont> fonts) {
        this.fonts = fonts;
    }
}
