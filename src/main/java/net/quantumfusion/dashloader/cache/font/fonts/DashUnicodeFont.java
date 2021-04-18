package net.quantumfusion.dashloader.cache.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashRegistry;

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
        font.images.forEach((identifier, nativeImage) -> images.put(registry.createIdentifierPointer(identifier), registry.createImagePointer(nativeImage)));
        this.sizes = font.sizes;
    }


    public UnicodeFont toUndash(DashRegistry registry) {
        Map<Identifier, NativeImage> out = new HashMap<>();
        images.entrySet().parallelStream().forEach((entry) -> out.put(registry.getIdentifier(entry.getKey()), registry.getImage(entry.getValue())));
        return new UnicodeFont(out, sizes);
    }
}
