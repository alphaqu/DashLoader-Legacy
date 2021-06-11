package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;

public class RegistryFontData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "fonts")
    public Pntr2ObjectMap<DashFont> fonts;

    public RegistryFontData(@Deserialize("fonts") Pntr2ObjectMap<DashFont> fonts) {
        this.fonts = fonts;
    }

    public RegistryFontData(Int2ObjectMap<DashFont> fonts) {
        this.fonts = new Pntr2ObjectMap<>(fonts);
    }

    public Int2ObjectMap<DashFont> toUndash() {
        return fonts.convert();
    }
}
