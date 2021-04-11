package net.quantumfusion.dash.cache.atlas;

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
                                         @Deserialize("interpolate") boolean interpolate
    ) {
        this.frames = frames;
        this.width = width;
        this.height = height;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolate = interpolate;
    }

    public DashAnimationResourceMetadata(AnimationResourceMetadata animationResourceMetadata) {
        frames = new ArrayList<>();
        AnimationResourceMetadataAccessor metadataAccessor = ((AnimationResourceMetadataAccessor) animationResourceMetadata);
        metadataAccessor.getFramesD().forEach(animationFrameResourceMetadata -> frames.add(new DashAnimationFrameResourceMetadata(animationFrameResourceMetadata)));
        width = metadataAccessor.getWidthD();
        height = metadataAccessor.getHeightD();
        defaultFrameTime = metadataAccessor.getDefaultFrameTimeD();
        interpolate = metadataAccessor.interpolateD();
    }

    public AnimationResourceMetadata toUndash() {
        ArrayList<AnimationFrameResourceMetadata> framesOut = new ArrayList<>();
        frames.forEach(dashAnimationFrameResourceMetadata -> framesOut.add(dashAnimationFrameResourceMetadata.toUndash()));
        return new AnimationResourceMetadata(framesOut, width, height, defaultFrameTime, interpolate);
    }
}
