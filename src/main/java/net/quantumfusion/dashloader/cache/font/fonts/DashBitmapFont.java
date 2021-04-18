package net.quantumfusion.dashloader.cache.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.quantumfusion.dashloader.cache.DashRegistry;

import java.util.HashMap;

public class DashBitmapFont implements DashFont {
    @Serialize(order = 0)
    public final Integer image;
    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public HashMap<Integer, DashBitmapFontGlyph> glyphs;

    public DashBitmapFont(@Deserialize("image") Integer image,
                          @Deserialize("glyphs") HashMap<Integer, DashBitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public DashBitmapFont(BitmapFont bitmapFont,DashRegistry registry) {
        this.image = registry.createImagePointer(bitmapFont.image);
        glyphs = new HashMap<>();
        bitmapFont.glyphs.forEach((integer, bitmap) -> glyphs.put(integer, new DashBitmapFontGlyph(bitmap, registry)));
    }

    public BitmapFont toUndash(DashRegistry registry) {
        Int2ObjectMap<BitmapFont.BitmapFontGlyph> out = new Int2ObjectOpenHashMap<>();
        glyphs.forEach((integer, bitmap) -> out.put(integer, bitmap.toUndash(registry)));
        return new BitmapFont(registry.getImage(image), out);
    }

}
