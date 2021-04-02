package net.quantumfusion.dash.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dash.sprite.util.DashImage;

public class DashBitmapFontGlyph {
    @Serialize(order = 0)
    public final float scaleFactor;
    @Serialize(order = 1)
    public final DashImage image;
    @Serialize(order = 2)
    public final int x;
    @Serialize(order = 3)
    public final int y;
    @Serialize(order = 4)
    public final int width;
    @Serialize(order = 5)
    public final int height;
    @Serialize(order = 6)
    public final int advance;
    @Serialize(order = 7)
    public final int ascent;

    public DashBitmapFontGlyph(@Deserialize("scaleFactor") float scaleFactor,
                               @Deserialize("image") DashImage image,
                               @Deserialize("x") int x,
                               @Deserialize("y") int y,
                               @Deserialize("width") int width,
                               @Deserialize("height") int height,
                               @Deserialize("advance") int advance,
                               @Deserialize("ascent") int ascent
    ) {
        this.scaleFactor = scaleFactor;
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.advance = advance;
        this.ascent = ascent;
    }

    public DashBitmapFontGlyph(BitmapFont.BitmapFontGlyph bitmapFontGlyph) {
        this.scaleFactor = bitmapFontGlyph.scaleFactor;
        this.image = new DashImage(bitmapFontGlyph.image);
        this.x = bitmapFontGlyph.x;
        this.y = bitmapFontGlyph.y;
        this.width = bitmapFontGlyph.width;
        this.height = bitmapFontGlyph.height;
        this.advance = bitmapFontGlyph.advance;
        this.ascent = bitmapFontGlyph.ascent;
    }

    public BitmapFont.BitmapFontGlyph toUndash() {
        return new BitmapFont.BitmapFontGlyph(scaleFactor, image.toUndash(), x, y, width, height, advance, ascent);
    }
}
