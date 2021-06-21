package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

public class RegistryFontData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "fonts")
    public Pointer2ObjectMap<DashFont> fonts;

    public RegistryFontData(@Deserialize("fonts") Pointer2ObjectMap<DashFont> fonts) {
        this.fonts = fonts;
    }

    public Int2ObjectMap<DashFont> toUndash() {
        return fonts.convert();
    }

}
