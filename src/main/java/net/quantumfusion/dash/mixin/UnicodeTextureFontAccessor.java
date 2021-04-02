package net.quantumfusion.dash.mixin;

import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(UnicodeTextureFont.class)
public interface UnicodeTextureFontAccessor {

    @Accessor("resourceManager")
    ResourceManager getResourceManager();

    @Accessor("sizes")
    byte[] getSizes();

    @Accessor("template")
    String getTemplate();

    @Accessor("images")
    Map<Identifier, NativeImage> getImages();
}
