package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AnimationResourceMetadata.class)
public interface AnimationResourceMetadataAccessor {

    @Accessor("frames")
    List<AnimationFrameResourceMetadata> getFramesD();

    @Accessor("width")
    int getWidthD();

    @Accessor("height")
    int getHeightD();

    @Accessor("defaultFrameTime")
    int getDefaultFrameTimeD();

    @Accessor("interpolate")
    boolean interpolateD();
}
