package net.quantumfusion.dashloader.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BitmapFont.class)
public interface BitmapFontAccessor {


    @Accessor("glyphs")
    Int2ObjectMap<RenderableGlyph> getGlyphs();

    @Accessor("image")
    NativeImage getImage();

}
