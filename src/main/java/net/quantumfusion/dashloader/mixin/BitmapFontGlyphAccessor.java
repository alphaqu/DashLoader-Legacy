package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BitmapFont.BitmapFontGlyph.class)
public interface BitmapFontGlyphAccessor {


    @Invoker("<init>")
    static BitmapFont.BitmapFontGlyph init(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
        throw new AssertionError();
    };

    @Accessor("image")
    NativeImage getImageD();

    @Accessor("x")
    int getXD();

    @Accessor("y")
    int getYD();

    @Accessor("scaleFactor")
    float getScaleFactorD();


    @Accessor("width")
    int getWidthD();

    @Accessor("height")
    int getHeightD();

    @Accessor("advance")
    int getAdvanceD();

    @Accessor("ascent")
    int getAscentD();
}
