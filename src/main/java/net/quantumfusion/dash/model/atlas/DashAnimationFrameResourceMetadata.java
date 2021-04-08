package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;

public class DashAnimationFrameResourceMetadata {
	private final int index;
	private final int time;

	public DashAnimationFrameResourceMetadata(int index, int time) {
		this.index = index;
		this.time = time;
	}

	public DashAnimationFrameResourceMetadata(AnimationFrameResourceMetadata animationFrameResourceMetadata) {
		index = animationFrameResourceMetadata.getIndex();
		time = animationFrameResourceMetadata.getTime();
	}

	public AnimationFrameResourceMetadata toUndash() {
		return new AnimationFrameResourceMetadata(index,time);
	}
}
