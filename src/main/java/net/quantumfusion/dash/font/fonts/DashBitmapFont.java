package net.quantumfusion.dash.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.quantumfusion.dash.sprite.util.DashImage;

import java.util.HashMap;

public class DashBitmapFont implements Dashable {
    @Serialize(order = 0)
    public final DashImage image;
    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public HashMap<Integer, DashBitmapFontGlyph> glyphs;

    public DashBitmapFont(@Deserialize("image") DashImage image,
                          @Deserialize("glyphs") HashMap<Integer, DashBitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public DashBitmapFont(BitmapFont bitmapFont) {
        this.image = new DashImage(bitmapFont.image);
        glyphs = new HashMap<>();
        bitmapFont.glyphs.forEach((integer, bitmapFontGlyph) -> glyphs.put(integer, new DashBitmapFontGlyph(bitmapFontGlyph)));
    }

    public BitmapFont toUndash() {
        Int2ObjectMap<BitmapFont.BitmapFontGlyph> out = new Int2ObjectOpenHashMap<>();
        glyphs.forEach((integer, dashBitmapFontGlyph) -> out.put(integer, dashBitmapFontGlyph.toUndash()));
        return new BitmapFont(image.toUndash(), out);
    }
}
