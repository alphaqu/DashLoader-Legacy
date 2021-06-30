package net.quantumfusion.dashloader.font;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.font.BitmapFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.mixin.accessor.BitmapFontAccessor;

public class DashBitmapFont implements DashFont {
    @Serialize(order = 0)
    public final int image;
    @Serialize(order = 1)
    public final Pointer2ObjectMap<DashBitmapFontGlyph> glyphs;

    public DashBitmapFont(@Deserialize("image") int image,
                          @Deserialize("glyphs") Pointer2ObjectMap<DashBitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public DashBitmapFont(BitmapFont bitmapFont, DashRegistry registry) {
        BitmapFontAccessor font = ((BitmapFontAccessor) bitmapFont);
        image = registry.createImagePointer(font.getImage());
        glyphs = new Pointer2ObjectMap<>();
        font.getGlyphs().forEach((integer, bitmapFontGlyph) -> glyphs.put(integer, new DashBitmapFontGlyph(bitmapFontGlyph, registry)));
    }

    public BitmapFont toUndash(DashRegistry registry) {
        Int2ObjectOpenHashMap<BitmapFont.BitmapFontGlyph> out = new Int2ObjectOpenHashMap<>();
        glyphs.forEach((entry) -> out.put(entry.key, entry.value.toUndash(registry)));
        return BitmapFontAccessor.init(registry.getImage(image), out);
    }

}
