package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MipmapHelper.class)
public abstract class MipmapHelperMixin {

	@Shadow
	private static int blend(int one, int two, int three, int four, boolean checkAlpha) {
		return 0;
	}

	@Inject(method = "getMipmapLevelsImages(Lnet/minecraft/client/texture/NativeImage;I)[Lnet/minecraft/client/texture/NativeImage;",
			at = @At(value = "HEAD"), cancellable = true)
	private static void mapmapFast(NativeImage image, int mipmap, CallbackInfoReturnable<NativeImage[]> cir) {
		NativeImage[] nativeImages = new NativeImage[mipmap + 1];
		nativeImages[0] = image;
		if (mipmap > 0) {
			boolean bl = false;

			int k;
			label51:
			for(k = 0; k < image.getWidth(); ++k) {
				for(int j = 0; j < image.getHeight(); ++j) {
					if (image.getPixelColor(k, j) >> 24 == 0) {
						bl = true;
						break label51;
					}
				}
			}
			for(k = 1; k <= mipmap; ++k) {
				NativeImage nativeImage = nativeImages[k - 1];
				NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
				int l = nativeImage2.getWidth();
				int m = nativeImage2.getHeight();

				for(int n = 0; n < l; ++n) {
					for(int o = 0; o < m; ++o) {
						nativeImage2.setPixelColor(n, o, blend(nativeImage.getPixelColor(n * 2, o * 2), nativeImage.getPixelColor(n * 2 + 1, o * 2), nativeImage.getPixelColor(n * 2, o * 2 + 1), nativeImage.getPixelColor(n * 2 + 1, o * 2 + 1), bl));
					}
				}

				nativeImages[k] = nativeImage2;
			}
		}

		cir.setReturnValue(nativeImages);
	}
}
