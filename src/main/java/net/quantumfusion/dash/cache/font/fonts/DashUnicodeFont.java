package net.quantumfusion.dash.cache.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.DashRegistry;
import net.quantumfusion.dash.cache.atlas.DashImage;

import java.util.HashMap;
import java.util.Map;

public class DashUnicodeFont implements DashFont {
    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public final Map<Integer, Integer> images;

    @Serialize(order = 1)
    public final byte[] sizes;


    public DashUnicodeFont(@Deserialize("images") Map<Integer, Integer> images,
                           @Deserialize("sizes") byte[] sizes) {
        this.images = images;
        this.sizes = sizes;
    }

    public DashUnicodeFont(UnicodeFont font, DashRegistry registry) {
        images = new HashMap<>();
        font.images.forEach((identifier, nativeImage) -> images.put(registry.createIdentifierPointer(identifier), registry.createFontImagePointer(nativeImage)));
        this.sizes = font.sizes;
    }


    public UnicodeFont toUndash(DashRegistry registry) {
        Map<Identifier, NativeImage> out = new HashMap<>();
        images.forEach((dashIdentifier, dashImage) -> out.put(registry.getIdentifier(dashIdentifier), registry.getFontImage(dashImage)));
        return new UnicodeFont(out, sizes);
    }
}
