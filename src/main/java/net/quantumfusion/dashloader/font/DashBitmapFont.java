package net.quantumfusion.dashloader.font;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.font.BitmapFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.BitmapFontAccessor;
import net.quantumfusion.dashloader.util.PairMap;

public class DashBitmapFont implements DashFont {
    @Serialize(order = 0)
    public final Long image;
    @Serialize(order = 1)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public PairMap<Integer, DashBitmapFontGlyph> glyphs;

    public DashBitmapFont(@Deserialize("image") Long image,
                          @Deserialize("glyphs") PairMap<Integer, DashBitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public DashBitmapFont(BitmapFont bitmapFont, DashRegistry registry) {
        BitmapFontAccessor font = ((BitmapFontAccessor) bitmapFont);
        image = registry.createImagePointer(font.getImage());
        glyphs = new PairMap<>();
        font.getGlyphs().forEach((integer, bitmapFontGlyph) -> glyphs.put(integer, new DashBitmapFontGlyph(bitmapFontGlyph, registry)));
    }

    public BitmapFont toUndash(DashRegistry registry) {
        Int2ObjectOpenHashMap<BitmapFont.BitmapFontGlyph> out = new Int2ObjectOpenHashMap();
        glyphs.forEach((integer, dashBitmapFontGlyph) -> out.put(integer, dashBitmapFontGlyph.toUndash(registry)));
        return BitmapFontAccessor.init(registry.getImage(image), out);
    }

}
