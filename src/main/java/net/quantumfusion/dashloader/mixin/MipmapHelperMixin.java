package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import net.quantumfusion.dashloader.mixin.accessor.NativeImageAccessor;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MipmapHelper.class)
public abstract class MipmapHelperMixin {

    @Shadow
    private static int blend(int one, int two, int three, int four, boolean checkAlpha) {
        throw new AssertionError();
    }

    /**
     * @author alphaqu, leocth
     * @reason DashLoader needs to replace the resource loading process completely. Thus, an overwrite is needed.
     */
    @Overwrite
    public static NativeImage[] getMipmapLevelsImages(NativeImage image, int mipmap) {
        NativeImage[] nativeImages = new NativeImage[mipmap + 1];
        nativeImages[0] = image;
        if (mipmap > 0) {
            boolean hasOpaquePixel = false;
            gotOpaquePixel:
            for (int pX = image.getWidth(); --pX >= 0;) {
                for (int pY = image.getHeight(); --pY >= 0;) {
                    if (image.getPixelColor(pX, pY) >> 24 == 0) {
                        hasOpaquePixel = true;
                        break gotOpaquePixel;
                    }
                }
            }

            for (int i = 0; i < mipmap; i++) {
                NativeImage srcImage = nativeImages[i];
                NativeImage outImage = new NativeImage(srcImage.getWidth() >> 1, srcImage.getHeight() >> 1, false);


                for (int pX = outImage.getWidth(); --pX >= 0;) {
                    for (int pY = outImage.getHeight(); --pY >= 0;) {
                        outImage.setPixelColor(
                            pX, pY,
                            blend(
                                srcImage.getPixelColor(pX * 2, pY * 2),
                                srcImage.getPixelColor(pX * 2 + 1, pY * 2),
                                srcImage.getPixelColor(pX * 2, pY * 2 + 1),
                                srcImage.getPixelColor(pX * 2 + 1, pY * 2 + 1),
                                hasOpaquePixel
                            )
                        );
                    }
                }

                nativeImages[i + 1] = outImage;
            }
        }

        return nativeImages;
    }
}
