package net.quantumfusion.dashloader.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.font.fonts.DashFont;

import java.util.Map;

public class RegistryFontData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Map<Long, DashFont> fonts;

    public RegistryFontData(@Deserialize("fonts") Map<Long, DashFont> fonts) {
        this.fonts = fonts;
    }
}
