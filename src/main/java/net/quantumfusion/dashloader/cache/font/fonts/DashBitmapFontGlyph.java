package net.quantumfusion.dashloader.cache.font.fonts;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.font.BitmapFont;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.BitmapFontGlyphAccessor;

public class DashBitmapFontGlyph {
    @Serialize(order = 0)
    public final float scaleFactor;
    @Serialize(order = 1)
    public final Integer image;
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
                               @Deserialize("image") Integer image,
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

    public DashBitmapFontGlyph(BitmapFont.BitmapFontGlyph bitmapFontGlyph, DashRegistry registry) {
        BitmapFontGlyphAccessor font = ((BitmapFontGlyphAccessor)(Object)bitmapFontGlyph);
        this.scaleFactor = font.getScaleFactorD();
        this.image = registry.createImagePointer(font.getImageD());
        this.x = font.getXD();
        this.y = font.getXD();
        this.width = font.getWidthD();
        this.height = font.getHeightD();
        this.advance = font.getAdvanceD();
        this.ascent = font.getAscentD();
    }

    public BitmapFont.BitmapFontGlyph toUndash(DashRegistry registry) {
        return BitmapFontGlyphAccessor.init(scaleFactor, registry.getImage(image), x, y, width, height, advance, ascent);
    }
}
