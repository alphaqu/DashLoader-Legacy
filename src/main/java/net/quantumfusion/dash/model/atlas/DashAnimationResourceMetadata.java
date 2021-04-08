package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.quantumfusion.dash.mixin.AnimationResourceMetadataAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashAnimationResourceMetadata {
    private final List<DashAnimationFrameResourceMetadata> frames;
    private final int width;
    private final int height;
    private final int defaultFrameTime;
    private final boolean interpolate;

    public DashAnimationResourceMetadata(List<DashAnimationFrameResourceMetadata> frames, int width, int height, int defaultFrameTime, boolean interpolate) {
        this.frames = frames;
        this.width = width;
        this.height = height;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolate = interpolate;
    }

    public DashAnimationResourceMetadata(AnimationResourceMetadata animationResourceMetadata) {
        frames = new ArrayList<>();
        AnimationResourceMetadataAccessor metadataAccessor = ((AnimationResourceMetadataAccessor)animationResourceMetadata);
        metadataAccessor.getFramesD().forEach(animationFrameResourceMetadata -> frames.add(new DashAnimationFrameResourceMetadata(animationFrameResourceMetadata)));
        width = metadataAccessor.getWidthD();
        height = metadataAccessor.getHeightD();
        defaultFrameTime = metadataAccessor.getDefaultFrameTimeD();
        interpolate = metadataAccessor.interpolateD();
    }

    public AnimationResourceMetadata toUndash() {
        List<AnimationFrameResourceMetadata> framesOut = new ArrayList<>();
        frames.forEach(dashAnimationFrameResourceMetadata -> framesOut.add(dashAnimationFrameResourceMetadata.toUndash()));
        return new AnimationResourceMetadata(framesOut,width,height,defaultFrameTime,interpolate);
    }
}
