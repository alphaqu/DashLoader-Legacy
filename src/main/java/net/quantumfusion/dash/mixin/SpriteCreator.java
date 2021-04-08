package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sprite.class)
public class SpriteCreator {

	public SpriteCreator(SpriteAtlasTexture atlas,
						 Sprite.Info info,
						 AnimationResourceMetadata animationMetadata,
						 NativeImage[] images,
						 int[] frameXs,
						 int[] frameYs,
						 SpriteInterpolationAccessor interpolation,
						 int x,
						 int y,
						 float uMin,
						 float uMax,
						 float vMin,
						 float vMax,
						 int frameIndex,
						 int frameTicks) {

	}
}
