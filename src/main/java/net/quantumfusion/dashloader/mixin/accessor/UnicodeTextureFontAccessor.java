package net.quantumfusion.dashloader.mixin.accessor;

import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(UnicodeTextureFont.class)
public interface UnicodeTextureFontAccessor {

    @Accessor
    byte[] getSizes();

    @Accessor
    String getTemplate();

    @Accessor
    Map<Identifier, NativeImage> getImages();

    @Mutable
    @Accessor
    void setSizes(byte[] sizes);


    @Accessor
    @Mutable
    void setTemplate(String template);

    @Accessor
    @Mutable
    void setResourceManager(ResourceManager resourceManager);

    @Accessor
    @Mutable
    void setImages(Map<Identifier, NativeImage> images);
}
