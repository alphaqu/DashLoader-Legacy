package net.quantumfusion.dash.sprite.info;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;

public class DashAnimationFrameResourceMetadata {

    @Serialize(order = 0)
    public final int index;
    @Serialize(order = 1)
    public final int time;

    public DashAnimationFrameResourceMetadata(@Deserialize("index") int index,
                                              @Deserialize("time") int time) {
        this.index = index;
        this.time = time;
    }


    public DashAnimationFrameResourceMetadata(AnimationFrameResourceMetadata animationFrameResourceMetadata) {
        index = animationFrameResourceMetadata.getIndex();
        time = animationFrameResourceMetadata.getTime();
    }


    public AnimationFrameResourceMetadata toUndash() {
        return new AnimationFrameResourceMetadata(index, time);
    }
}
