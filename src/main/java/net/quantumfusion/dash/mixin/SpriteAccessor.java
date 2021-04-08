package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.class)
public interface SpriteAccessor {

	@Accessor("atlas")
	SpriteAtlasTexture getAtlas();

	@Accessor("info")
	Sprite.Info getInfo();

	@Accessor("animationMetadata")
	AnimationResourceMetadata getAnimationMetadata();

	@Accessor("images")
	NativeImage[] getImages();

	@Accessor("frameXs")
	int[] getFrameXs();

	@Accessor("frameYs")
	int[] getFrameYs();

	@Accessor("interpolation")
	Sprite.Interpolation getInterpolation();

	@Accessor("x")
	int getX();

	@Accessor("y")
	int getY();

	@Accessor("uMin")
	float getUMin();

	@Accessor("uMax")
	float getUMax();

	@Accessor("vMin")
	float getVMin();

	@Accessor("vMax")
	float getVMax();

	@Accessor("frameIndex")
	int getFrameIndex();

	@Accessor("frameTicks")
	int getFrameTicks();



	@Accessor("atlas")
	void setAtlas(SpriteAtlasTexture atlas);

	@Accessor("info")
	void setInfo(Sprite.Info info);

	@Accessor("animationMetadata")
	void setAnimationMetadata(AnimationResourceMetadata animationMetadata);

	@Accessor("images")
	void setImages(NativeImage[] images);

	@Accessor("frameXs")
	void setFrameXs(int[]frameXs);

	@Accessor("frameYs")
	void setFrameYs(int[] frameYs);

	@Accessor("interpolation")
	void setInterpolation(Sprite.Interpolation interpolation);

	@Accessor("x")
	void setX(int x);

	@Accessor("y")
	void setY(int y);

	@Accessor("uMin")
	void setUMin(float uMin);

	@Accessor("uMax")
	void setUMax(float uMax);

	@Accessor("vMin")
	void setVMin(float vMin);

	@Accessor("vMax")
	void setVMax(float vMax);

	@Accessor("frameIndex")
	void setFrameIndex(int frameIndex);

	@Accessor("frameTicks")
	void setFrameTicks(int frameTicks );

	@Invoker("<init>")
	static Sprite newSprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
		throw new AssertionError();
	}
}
