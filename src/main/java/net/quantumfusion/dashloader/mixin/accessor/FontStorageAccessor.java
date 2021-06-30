package net.quantumfusion.dashloader.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontStorage.class)
public interface FontStorageAccessor {

    @Accessor
    Identifier getId();

    @Accessor
    void setBlankGlyphRenderer(GlyphRenderer renderer);

    @Accessor
    void setWhiteRectangleGlyphRenderer(GlyphRenderer renderer);

    @Accessor
    Int2ObjectMap<GlyphRenderer> getGlyphRendererCache();

    @Accessor
    Int2ObjectMap<Glyph> getGlyphCache();

    @Accessor
    Int2ObjectMap<IntList> getCharactersByWidth();


    @Accessor
    Glyph getSPACE();

    @Invoker
    GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Invoker
    void closeFonts();

    @Invoker
    void closeGlyphAtlases();
}
