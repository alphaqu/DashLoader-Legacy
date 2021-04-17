package net.quantumfusion.dashloader.cache.font.fonts;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.mixin.BitmapFontAccessor;
import net.quantumfusion.dashloader.mixin.BitmapFontGlyphAccessor;
import org.jetbrains.annotations.Nullable;

public class BitmapFont implements Font {
    public final NativeImage image;
    public final Int2ObjectMap<BitmapFontGlyph> glyphs;

    public BitmapFont(NativeImage image, Int2ObjectMap<BitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public BitmapFont(net.minecraft.client.font.BitmapFont font) {
        BitmapFontAccessor fontAccess = ((BitmapFontAccessor) font);
        image = fontAccess.getImage();
        Int2ObjectMap<BitmapFontGlyph> out = new Int2ObjectOpenHashMap<>();
        fontAccess.getGlyphs().forEach((integer, renderableGlyph) -> out.put(integer, new BitmapFontGlyph((BitmapFontGlyphAccessor) renderableGlyph)));
        glyphs = out;
    }

    public void close() {
        this.image.close();
    }

    @Nullable
    public RenderableGlyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable(this.glyphs.keySet());
    }



    @Environment(EnvType.CLIENT)
    public static final class BitmapFontGlyph implements RenderableGlyph {
        public final float scaleFactor;
        public final NativeImage image;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public final int advance;
        public final int ascent;

        public BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
            this.scaleFactor = scaleFactor;
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.advance = advance;
            this.ascent = ascent;
        }

        public BitmapFontGlyph(BitmapFontGlyphAccessor font) {
            scaleFactor = font.getScaleFactorD();
            image = font.getImageD();
            x = font.getXD();
            y = font.getYD();
            width = font.getWidthD();
            height = font.getHeightD();
            advance = font.getAdvanceD();
            ascent = font.getAscentD();
        }

        public float getOversample() {
            return 1.0F / this.scaleFactor;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public float getAdvance() {
            return (float) this.advance;
        }

        public float getAscent() {
            return RenderableGlyph.super.getAscent() + 7.0F - (float) this.ascent;
        }

        public void upload(int x, int y) {
            this.image.upload(0, x, y, this.x, this.y, this.width, this.height, false, false);
        }

        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }
    }

}
