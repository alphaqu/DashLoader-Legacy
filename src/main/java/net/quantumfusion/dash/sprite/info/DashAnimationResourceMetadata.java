package net.quantumfusion.dash.sprite.info;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.quantumfusion.dash.mixin.AnimationResourceMetadataAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashAnimationResourceMetadata {

	@Serialize(order = 0)
	public final List<DashAnimationFrameResourceMetadata> frames;
	@Serialize(order = 1)
	public final int width;
	@Serialize(order = 2)
	public final int height;
	@Serialize(order = 3)
	public final int defaultFrameTime;
	@Serialize(order = 4)
	public final boolean interpolate;

	public DashAnimationResourceMetadata(@Deserialize("frames") List<DashAnimationFrameResourceMetadata> frames,
										 @Deserialize("width") int width,
										 @Deserialize("height") int height,
										 @Deserialize("defaultFrameTime") int defaultFrameTime,
										 @Deserialize("interpolate") boolean interpolate) {
		this.frames = frames;
		this.width = width;
		this.height = height;
		this.defaultFrameTime = defaultFrameTime;
		this.interpolate = interpolate;
	}

	public DashAnimationResourceMetadata(AnimationResourceMetadata animationResourceMetadata) {
		AnimationResourceMetadataAccessor animationResourceMetadataAccessor = ((AnimationResourceMetadataAccessor) animationResourceMetadata);

		ArrayList<DashAnimationFrameResourceMetadata> framesOut = new ArrayList<>();
		animationResourceMetadataAccessor.getFramesD().forEach(animationFrameResourceMetadata -> framesOut.add(new DashAnimationFrameResourceMetadata(animationFrameResourceMetadata)));
		frames = framesOut;
		width = animationResourceMetadataAccessor.getWidthD();
		height = animationResourceMetadataAccessor.getHeightD();
		defaultFrameTime = animationResourceMetadataAccessor.getDefaultFrameTimeD();
		interpolate = animationResourceMetadataAccessor.interpolateD();
	}

	public AnimationResourceMetadata toUndash() {
		ArrayList<AnimationFrameResourceMetadata> framesOut = new ArrayList<>();
		frames.forEach(dashAnimationFrameResourceMetadata -> framesOut.add(dashAnimationFrameResourceMetadata.toUndash()));
		return new AnimationResourceMetadata(framesOut, width, height, defaultFrameTime, interpolate);
	}
}
