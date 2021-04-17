package net.quantumfusion.dashloader.cache.font.fonts;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.mixin.UnicodeTextureFontAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class UnicodeFont implements Font {
    private static final Logger LOGGER = LogManager.getLogger();
    public final Map<Identifier, NativeImage> images;
    public final byte[] sizes;
    private ResourceManager resourceManager;

    public UnicodeFont(UnicodeTextureFont font) {
        UnicodeTextureFontAccessor fontAccessor = ((UnicodeTextureFontAccessor) font);
        resourceManager = fontAccessor.getResourceManager();
        sizes = fontAccessor.getSizes();
        images = fontAccessor.getImages();
    }

    public UnicodeFont(Map<Identifier, NativeImage> images,
                       byte[] sizes) {
        this.images = images;
        this.sizes = sizes;
    }

    public UnicodeFont(ResourceManager resourceManager, byte[] sizes) {
        this.resourceManager = resourceManager;
        this.sizes = sizes;
        this.images = Maps.newHashMap();
        label324:
        for (int i = 0; i < 256; ++i) {
            int j = i * 256;
            Identifier identifier = this.getImageId(j);
            try {
                Resource resource = this.resourceManager.getResource(identifier);
                try (NativeImage nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream())) {
                    resource.close();
                    if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
                        int k = 0;

                        while (true) {
                            if (k >= 256) {
                                continue label324;
                            }

                            byte b = sizes[j + k];
                            if (b != 0 && getStart(b) > getEnd(b)) {
                                sizes[j + k] = 0;
                            }

                            ++k;
                        }
                    }
                }

            } catch (IOException ignored) {

            }

            Arrays.fill(sizes, j, j + 256, (byte) 0);
        }

    }

    private static int getStart(byte size) {
        return size >> 4 & 15;
    }

    private static int getEnd(byte size) {
        return (size & 15) + 1;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void close() {
        this.images.values().forEach(NativeImage::close);
    }

    private Identifier getImageId(int codePoint) {
        String id = Integer.toHexString((codePoint & (~0xFF)) >> 8);
        return (new Identifier("textures/font/unicode_page_" + (id.length() == 1 ? 0 + id : id) + ".png"));
    }

    @Nullable
    public RenderableGlyph getGlyph(int codePoint) {
        if (codePoint >= 0 && codePoint <= 65535) {
            byte b = this.sizes[codePoint];
            if (b != 0) {
                NativeImage nativeImage = this.images.computeIfAbsent(this.getImageId(codePoint), this::getGlyphImage);
                if (nativeImage != null) {
                    int i = getStart(b);
                    return new UnicodeTextureGlyph(codePoint % 16 * 16 + i, (codePoint & 255) / 16 * 16, getEnd(b) - i, 16, nativeImage);
                }
            }

        }
        return null;
    }

    public IntSet getProvidedGlyphs() {
        IntSet intSet = new IntOpenHashSet();

        for (int i = 0; i < 65535; ++i) {
            if (this.sizes[i] != 0) {
                intSet.add(i);
            }
        }

        return intSet;
    }

    @Nullable
    private NativeImage getGlyphImage(Identifier glyphId) {
        try {
            Resource resource = this.resourceManager.getResource(glyphId);
            NativeImage var4;
            try {
                var4 = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());
            } finally {
                if (resource != null) {
                    resource.close();
                }

            }

            return var4;
        } catch (IOException var16) {
            LOGGER.error("Couldn't load texture {}", glyphId, var16);
            return null;
        }
    }

    @Environment(EnvType.CLIENT)
    static class UnicodeTextureGlyph implements RenderableGlyph {
        private final int width;
        private final int height;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final NativeImage image;

        private UnicodeTextureGlyph(int x, int y, int width, int height, NativeImage image) {
            this.width = width;
            this.height = height;
            this.unpackSkipPixels = x;
            this.unpackSkipRows = y;
            this.image = image;
        }

        public float getOversample() {
            return 2.0F;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public float getAdvance() {
            return (float) (this.width / 2 + 1);
        }

        public void upload(int x, int y) {
            this.image.upload(0, x, y, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
        }

        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }

        public float getShadowOffset() {
            return 0.5F;
        }

        public float getBoldOffset() {
            return 0.5F;
        }
    }
}
