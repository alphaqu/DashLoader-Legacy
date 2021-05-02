package net.quantumfusion.dashloader.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {


    @Invoker("<init>")
    static NativeImage init(NativeImage.Format format, int width, int height, boolean useStb, long pointer) {
        throw new AssertionError();
    };
    @Accessor
    long getPointer();
}
