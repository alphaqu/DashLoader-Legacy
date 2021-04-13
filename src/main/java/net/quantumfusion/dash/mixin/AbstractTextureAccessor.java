package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {

    @Accessor
    boolean getBilinear();

    @Accessor
    boolean getMipmap();

    @Accessor
    void setBilinear(boolean bilinear);

    @Accessor
    void setMipmap(boolean mipmap);

    @Accessor
    void setGlId(int glId);
}
