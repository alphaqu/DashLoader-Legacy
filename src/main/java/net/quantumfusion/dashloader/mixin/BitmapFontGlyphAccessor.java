package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.font.BitmapFont$BitmapFontGlyph")
public interface BitmapFontGlyphAccessor {

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
