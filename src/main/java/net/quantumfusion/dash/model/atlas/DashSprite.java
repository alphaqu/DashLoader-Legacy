package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.mixin.SpriteAccessor;
import net.quantumfusion.dash.sprite.info.DashSpriteInfo;
import net.quantumfusion.dash.sprite.util.DashImage;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DashSprite {

	private final DashSpriteInfo info;
	private final DashAnimationResourceMetadata animationMetadata;
	protected List<DashImage> images;
	private final int[] frameXs;
	private final int[] frameYs;
	@Nullable
	private final DashSpriteInterpolation interpolation;
	private final int x;
	private final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private final int frameIndex;
	private final int frameTicks;

	public DashSprite(DashSpriteInfo info, DashAnimationResourceMetadata animationMetadata, List<DashImage> images, int[] frameXs, int[] frameYs, @Nullable DashSpriteInterpolation interpolation, int x, int y, float uMin, float uMax, float vMin, float vMax, int frameIndex, int frameTicks) {
		this.info = info;
		this.animationMetadata = animationMetadata;
		this.images = images;
		this.frameXs = frameXs;
		this.frameYs = frameYs;
		this.interpolation = interpolation;
		this.x = x;
		this.y = y;
		this.uMin = uMin;
		this.uMax = uMax;
		this.vMin = vMin;
		this.vMax = vMax;
		this.frameIndex = frameIndex;
		this.frameTicks = frameTicks;
	}

	public DashSprite(Sprite sprite) {
		SpriteAccessor spriteAccess = ((SpriteAccessor) sprite);
		info = new DashSpriteInfo(spriteAccess.getInfo());
		animationMetadata = new DashAnimationResourceMetadata(spriteAccess.getAnimationMetadata());
		images = Stream.of(spriteAccess.getImages()).map(DashImage::new).collect(Collectors.toList());
		frameXs = spriteAccess.getFrameXs();
		frameYs = spriteAccess.getFrameYs();
		Sprite.Interpolation interpolation = spriteAccess.getInterpolation();
		if(interpolation != null){
			this.interpolation = new DashSpriteInterpolation(spriteAccess.getInterpolation());
		} else {
			this.interpolation = null;
		}
		x = spriteAccess.getX();
		y = spriteAccess.getY();
		uMin = spriteAccess.getUMin();
		uMax = spriteAccess.getUMax();
		vMin = spriteAccess.getVMin();
		vMax = spriteAccess.getVMax();
		frameIndex = spriteAccess.getFrameIndex();
		frameTicks = spriteAccess.getFrameTicks();
	}

	public Sprite toUndash(SpriteAtlasTexture spriteAtlasTexture) {
		try {
			Sprite out = (Sprite) Dash.getUnsafe().allocateInstance(Sprite.class);
			SpriteAccessor spriteAccessor = ((SpriteAccessor) out);
			spriteAccessor.setAtlas(spriteAtlasTexture);
			spriteAccessor.setInfo(info.toUndash());
			spriteAccessor.setAnimationMetadata(animationMetadata.toUndash());
			ArrayList<NativeImage> imagesOut = new ArrayList<>();
			images.forEach(dashImage -> imagesOut.add(dashImage.toUndash()));
			spriteAccessor.setImages(imagesOut.toArray(new NativeImage[0]));
			spriteAccessor.setFrameXs(frameXs);
			spriteAccessor.setFrameYs(frameYs);
			if(interpolation != null){
				spriteAccessor.setInterpolation(interpolation.toUndash());
			} else {
				spriteAccessor.setInterpolation(null);
			}
			spriteAccessor.setX(x);
			spriteAccessor.setY(y);
			spriteAccessor.setUMin(uMin);
			spriteAccessor.setUMax(uMax);
			spriteAccessor.setVMin(vMin);
			spriteAccessor.setVMax(vMax);
			spriteAccessor.setFrameIndex(frameIndex);
			spriteAccessor.setFrameTicks(frameTicks);
			return out;
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}


}
