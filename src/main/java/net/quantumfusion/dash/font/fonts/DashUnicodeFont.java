package net.quantumfusion.dash.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.sprite.util.DashImage;

import java.util.HashMap;
import java.util.Map;

public class DashUnicodeFont {
    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public final Map<DashIdentifier, DashImage> images;

    @Serialize(order = 1)
    public final byte[] sizes;

    public DashUnicodeFont(@Deserialize("images") Map<DashIdentifier, DashImage> images,
                           @Deserialize("sizes") byte[] sizes) {
        this.images = images;
        this.sizes = sizes;
    }

    public DashUnicodeFont(UnicodeFont font) {
        images = new HashMap<>();
        font.images.forEach((identifier, nativeImage) -> images.put(new DashIdentifier(identifier), new DashImage(nativeImage)));
        this.sizes = font.sizes;
    }


    public UnicodeFont toUndash() {
        Map<Identifier, NativeImage> out = new HashMap<>();
        images.forEach((dashIdentifier, dashImage) -> out.put(dashIdentifier.toUndash(), dashImage.toUndash()));
        return new UnicodeFont(out, sizes);
    }
}
